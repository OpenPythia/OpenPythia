package org.openpythia.utilities.sql;

import org.openpythia.dbconnection.ConnectionPoolUtils;
import org.openpythia.progress.ProgressListener;
import org.openpythia.utilities.deltasql.DeltaSQLStatementSnapshot;
import org.openpythia.utilities.waitevent.WaitEventForStatementTuple;
import org.openpythia.utilities.waitevent.WaitEventForTimeSpanTuple;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLWaitLoadHelper {

    private final static int NUMBER_BIND_VARIABLES_SELECT_WAIT_EVENTS_SQL = 100;
    private final static String SELECT_WAIT_EVENTS_FOR_100_STATEMENTS = "SELECT h.sql_id, h.wait_class, h.event, " +
            "o.owner, o.object_name, sum(h.time_waited) / 1000000 "
            + "FROM gv$active_session_history h "
            + "LEFT JOIN dba_objects o ON h.current_obj# = o.object_id "
            + "WHERE session_state = 'WAITING' "
            + "AND sql_id IN ("
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            + "AND h.sample_time BETWEEN ? AND ? "
            + "GROUP BY h.sql_id, h.wait_class, h.event, o.owner, o.object_name "
            + "ORDER BY sql_id, sum(time_waited) DESC";

    private final static String SELECT_WAIT_EVENTS_FOR_TIME_SPAN = "SELECT h.event, h.wait_class, " +
            "o.owner, o.object_name, sum(h.time_waited) / 1000000 "
            + "FROM gv$active_session_history h "
            + "LEFT JOIN dba_objects o ON h.current_obj# = o.object_id "
            + "WHERE session_state = 'WAITING' "
            + "AND h.sample_time BETWEEN ? AND ? "
            + "GROUP BY h.event, h.wait_class, o.owner, o.object_name "
            + "ORDER BY sum(time_waited) DESC";

    protected static Map<DeltaSQLStatementSnapshot, List<WaitEventForStatementTuple>> loadWaitEventsForStatements(
            List<DeltaSQLStatementSnapshot> worstStatements,
            Calendar startTime,
            Calendar stopTime,
            ProgressListener progressListener) {

        Map<DeltaSQLStatementSnapshot, List<WaitEventForStatementTuple>> result = new HashMap<>();

        if (progressListener != null) {
            progressListener.setStartValue(0);
            progressListener.setEndValue(worstStatements.size());
            progressListener.setCurrentValue(0);
        }

        int progressCounter = 0;

        List<DeltaSQLStatementSnapshot> missingWaitEventSqlStatements = new ArrayList<>(worstStatements);
        List<DeltaSQLStatementSnapshot> sqlStatementsToLoad = new ArrayList<>();

        int numberStatements;

        while (missingWaitEventSqlStatements.size() > 0) {
            sqlStatementsToLoad.clear();
            numberStatements = 0;

            Iterator<DeltaSQLStatementSnapshot> iterator = missingWaitEventSqlStatements.iterator();

            while (iterator.hasNext() && numberStatements <= NUMBER_BIND_VARIABLES_SELECT_WAIT_EVENTS_SQL) {
                sqlStatementsToLoad.add(iterator.next());
                numberStatements++;
            }

            // remove those statements from the initial for which the loading is
            // going on
            for (DeltaSQLStatementSnapshot statement : sqlStatementsToLoad) {
                missingWaitEventSqlStatements.remove(statement);
            }

            loadWaitEventsForSQLStatements(sqlStatementsToLoad, startTime, stopTime, result);

            progressCounter += sqlStatementsToLoad.size();

            if (progressListener != null) {
                progressListener.setCurrentValue(progressCounter);
            }

            // sleep for 0.1 seconds so all the other tasks can do their
            // work
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // we don't care for being interrupted
            }
        }

        if (progressListener != null) {
            progressListener.informFinished();
        }

        return result;
    }

    private static void loadWaitEventsForSQLStatements(List<DeltaSQLStatementSnapshot> sqlStatementsToLoad,
                                                       Calendar startTime, Calendar stopTime,
                                                       Map<DeltaSQLStatementSnapshot, List<WaitEventForStatementTuple>> result) {

        Connection connection = null;
        try {
            connection = ConnectionPoolUtils.getConnectionFromPool();
            PreparedStatement sqlWaitEventsStatement = connection
                    .prepareStatement(SELECT_WAIT_EVENTS_FOR_100_STATEMENTS);
            sqlWaitEventsStatement.setFetchSize(1000);

            int indexPlaceholder = 1;
            for (DeltaSQLStatementSnapshot currentStatement : sqlStatementsToLoad) {
                sqlWaitEventsStatement.setString(indexPlaceholder,
                        currentStatement.getSqlStatement().getSqlId());
                indexPlaceholder++;

                if (indexPlaceholder > NUMBER_BIND_VARIABLES_SELECT_WAIT_EVENTS_SQL) {
                    // set the time window for the samples
                    sqlWaitEventsStatement.setDate(NUMBER_BIND_VARIABLES_SELECT_WAIT_EVENTS_SQL + 1, new java.sql.Date(startTime.getTimeInMillis()));
                    sqlWaitEventsStatement.setDate(NUMBER_BIND_VARIABLES_SELECT_WAIT_EVENTS_SQL + 2, new java.sql.Date(stopTime.getTimeInMillis()));

                    // all place holders are filled - so fetch the wait events from the DB
                    getWaitEventsFromDB(sqlStatementsToLoad, sqlWaitEventsStatement, result);

                    indexPlaceholder = 1;
                }
            }

            if (indexPlaceholder > 1) {
                // there are some statements left...
                // fill the empty bind variables with invalid addresses
                for (int index = indexPlaceholder; index <= NUMBER_BIND_VARIABLES_SELECT_WAIT_EVENTS_SQL; index++) {
                    sqlWaitEventsStatement.setString(index, "");
                }

                // set the time window for the samples
                sqlWaitEventsStatement.setDate(NUMBER_BIND_VARIABLES_SELECT_WAIT_EVENTS_SQL + 1, new java.sql.Date(startTime.getTimeInMillis()));
                sqlWaitEventsStatement.setDate(NUMBER_BIND_VARIABLES_SELECT_WAIT_EVENTS_SQL + 2, new java.sql.Date(stopTime.getTimeInMillis()));

                getWaitEventsFromDB(sqlStatementsToLoad, sqlWaitEventsStatement, result);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            ConnectionPoolUtils.returnConnectionToPool(connection);
        }
    }

    private static void getWaitEventsFromDB(
            List<DeltaSQLStatementSnapshot> sqlStatementsToLoad,
            PreparedStatement sqlTextStatement,
            Map<DeltaSQLStatementSnapshot, List<WaitEventForStatementTuple>> result) throws SQLException {

        ResultSet waitEventsResultSet = sqlTextStatement.executeQuery();

        // use a definitely not used (invalid) SQL ID
        String lastSqlId = "";
        DeltaSQLStatementSnapshot currentStatement = null;
        while (waitEventsResultSet.next()) {

            int resultIndex = 1;
            String currentSqlId = waitEventsResultSet.getString(resultIndex++);
            String currentEventWaitClass = waitEventsResultSet.getString(resultIndex++);
            String currentEvent = waitEventsResultSet.getString(resultIndex++);
            String currentOwner = waitEventsResultSet.getString(resultIndex++);
            String currentObject = waitEventsResultSet.getString(resultIndex++);
            BigDecimal currentWaitedSeconds = waitEventsResultSet.getBigDecimal(resultIndex++);

            if (!lastSqlId.equals(currentSqlId)) {
                // find the SQL statement for the returned SQL id
                for (DeltaSQLStatementSnapshot lookupStatement : sqlStatementsToLoad) {
                    if (lookupStatement.getSqlStatement().getSqlId().equals(currentSqlId)) {
                        currentStatement = lookupStatement;
                        break;
                    }
                }
                result.put(currentStatement, new ArrayList<WaitEventForStatementTuple>());
            }
            result.get(currentStatement).add(new WaitEventForStatementTuple(currentEventWaitClass, currentEvent,
                    currentOwner, currentObject, currentWaitedSeconds));

            lastSqlId = currentSqlId;
        }
    }

    protected static List<WaitEventForTimeSpanTuple> loadWaitEventsForTimeSpan(Calendar startTime,
                                                                               Calendar stopTime) {
        List<WaitEventForTimeSpanTuple> result = new ArrayList<>();

        Connection connection = null;
        try {
            connection = ConnectionPoolUtils.getConnectionFromPool();
            PreparedStatement sqlWaitEventsStatement = connection
                    .prepareStatement(SELECT_WAIT_EVENTS_FOR_TIME_SPAN);
            sqlWaitEventsStatement.setFetchSize(1000);

            sqlWaitEventsStatement.setDate(1, new java.sql.Date(startTime.getTimeInMillis()));
            sqlWaitEventsStatement.setDate(2, new java.sql.Date(stopTime.getTimeInMillis()));

            ResultSet waitEventsResultSet = sqlWaitEventsStatement.executeQuery();

            while (waitEventsResultSet.next()) {

                int resultIndex = 1;
                String currentEvent = waitEventsResultSet.getString(resultIndex++);
                String currentEventWaitClass = waitEventsResultSet.getString(resultIndex++);
                String currentOwner = waitEventsResultSet.getString(resultIndex++);
                String currentObject = waitEventsResultSet.getString(resultIndex++);
                BigDecimal currentWaitedSeconds = waitEventsResultSet.getBigDecimal(resultIndex++);

                result.add(new WaitEventForTimeSpanTuple(
                        currentEventWaitClass,
                        currentEvent,
                        currentOwner,
                        currentObject,
                        currentWaitedSeconds));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            ConnectionPoolUtils.returnConnectionToPool(connection);
        }

        return result;
    }
}
