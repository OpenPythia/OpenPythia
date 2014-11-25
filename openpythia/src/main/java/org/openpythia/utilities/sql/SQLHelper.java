/**
 * Copyright 2012 msg systems ag
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package org.openpythia.utilities.sql;

import org.openpythia.dbconnection.ConnectionPoolUtils;
import org.openpythia.progress.ProgressListener;
import org.openpythia.utilities.deltasql.DeltaSQLStatementSnapshot;
import org.openpythia.utilities.waitevent.WaitEventTuple;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SQLHelper {

    private final static String NUMBER_STATEMENTS_IN_LIBRARY_CACHE = "SELECT COUNT(*) "
            + "FROM gv$sqlarea";

    private final static String DATE_TIME_DATABASE = "SELECT sysdate "
            + "FROM dual";

    private final static int NUMBER_BIND_VARIABLES_SELECT_SQL_TEXT = 200;
    private final static String SELECT_SQL_TEXT_FOR_200_STATEMENTS = "SELECT DISTINCT sql_id, sql_text, piece "
            + "FROM gv$sqltext_with_newlines "
            + "WHERE sql_id IN ("
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " + "ORDER BY sql_id, piece";

    private final static int NUMBER_BIND_VARIABLES_SELECT_EXECUTION_PLAN = 100;
    private final static String SELECT_EXECUTION_PLANS_FOR_100_STATEMENTS = "SELECT sql_id, inst_id, child_number, id, parent_id, operation, "
            + "options, object_owner, object_name, depth, position, cost, cardinality, "
            + "bytes, cpu_cost, io_cost, access_predicates, filter_predicates "
            + "FROM gv$sql_plan "
            + "WHERE sql_id IN ("
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
            + "ORDER BY sql_id, inst_id, child_number, id, position";

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
            + "AND sample_time BETWEEN ? AND ? "
            + "GROUP BY h.sql_id, h.wait_class, h.event, o.owner, o.object_name "
            + "ORDER BY sql_id, sum(time_waited) DESC";

    private static List<SQLStatement> allSQLStatements = new ArrayList<>();
    private static List<SQLStatement> unloadedSQLStatements = new CopyOnWriteArrayList<>();

    private static SQLStatementLoader sqlStatementLoader;

    public static SQLStatement getSQLStatement(String sqlId, String address,
                                               String parsingSchema, int instance) {
        SQLStatement result;

        SQLStatement newStatement = new SQLStatement(sqlId, address,
                parsingSchema, instance);

        if (allSQLStatements.contains(newStatement)) {
            // reuse of an existing statement
            result = allSQLStatements.get(allSQLStatements.indexOf(newStatement));
        } else {
            allSQLStatements.add(newStatement);
            unloadedSQLStatements.add(newStatement);
            result = newStatement;
        }

        return result;
    }

    public static SQLStatement getRegisterSQLStatement(SQLStatement sqlStatement) {
        SQLStatement result = getSQLStatement(
                sqlStatement.getSqlId(),
                sqlStatement.getAddress(),
                sqlStatement.getParsingSchema(),
                sqlStatement.getInstanceId());

        if (sqlStatement.getSqlText() != null) {
            result.setSqlText(sqlStatement.getSqlText());
        }

        return result;
    }

    public static void loadSQLTextForStatements(List<SQLStatement> sqlStatements, ProgressListener progressListener) {
        if (progressListener != null) {
            progressListener.setStartValue(0);
            progressListener.setEndValue(sqlStatements.size());
            progressListener.setCurrentValue(0);
        }

        List<SQLStatement> sqlStatementsToLoad = new ArrayList<>();
        int numberStatements = 0;
        int progressCounter = 0;
        for (SQLStatement statement : sqlStatements) {

            sqlStatementsToLoad.add(statement);
            numberStatements++;
            if (numberStatements == NUMBER_BIND_VARIABLES_SELECT_SQL_TEXT) {

                loadSQLStatements(sqlStatementsToLoad);

                sqlStatementsToLoad.clear();
                numberStatements = 0;

                if (progressListener != null) {
                    progressListener.setCurrentValue(progressCounter);
                }
            }

            progressCounter++;
        }

        if (sqlStatementsToLoad.size() > 0) {
            // there are some statements left to load
            loadSQLStatements(sqlStatementsToLoad);
        }

        if (progressListener != null) {
            progressListener.informFinished();
        }
    }

    public static synchronized void startSQLTextLoader() {
        if (sqlStatementLoader == null) {
            sqlStatementLoader = new SQLStatementLoader();
            new Thread(sqlStatementLoader).start();
        }
    }

    public static Map<DeltaSQLStatementSnapshot, List<WaitEventTuple>> loadWaitEventsForStatements(
            List<DeltaSQLStatementSnapshot> worstStatements,
            Calendar startTime,
            Calendar stopTime,
            ProgressListener progressListener) {

        Map<DeltaSQLStatementSnapshot, List<WaitEventTuple>> result = new HashMap<>();

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
                                                       Map<DeltaSQLStatementSnapshot, List<WaitEventTuple>> result) {

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
            Map<DeltaSQLStatementSnapshot, List<WaitEventTuple>> result) throws SQLException {

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
                result.put(currentStatement, new ArrayList<WaitEventTuple>());
            }
            result.get(currentStatement).add(new WaitEventTuple(currentEventWaitClass, currentEvent,
                    currentOwner, currentObject, currentWaitedSeconds));

            lastSqlId = currentSqlId;
        }
    }

    private static class SQLStatementLoader implements Runnable {

        public void run() {
            List<SQLStatement> sqlStatementsToLoad = new ArrayList<>();
            int numberStatements;

            while (true) {
                if (unloadedSQLStatements.size() > 0) {

                    sqlStatementsToLoad.clear();
                    numberStatements = 0;

                    Iterator<SQLStatement> iterator = unloadedSQLStatements.iterator();

                    while (iterator.hasNext() && numberStatements <= NUMBER_BIND_VARIABLES_SELECT_SQL_TEXT) {
                        sqlStatementsToLoad.add(iterator.next());
                        numberStatements++;
                    }

                    loadSQLStatements(sqlStatementsToLoad);

                    // remove the loaded statements from the list
                    for(SQLStatement statement : sqlStatementsToLoad) {
                        unloadedSQLStatements.remove(statement);
                    }
                }

                // sleep for 0.2 seconds so all the other tasks can do their work
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // we don't care for being interrupted
                }
            }
        }
    }

    private static void loadSQLStatements(List<SQLStatement> sqlStatementsToLoad) {

        Connection connection = null;
        try {
            connection = ConnectionPoolUtils.getConnectionFromPool();
            PreparedStatement sqlTextStatement = connection.prepareStatement(SELECT_SQL_TEXT_FOR_200_STATEMENTS);
            sqlTextStatement.setFetchSize(1000);

            int indexPlaceholder = 1;
            for (SQLStatement currentStatement : sqlStatementsToLoad) {
                sqlTextStatement.setString(indexPlaceholder, currentStatement.getSqlId());
                indexPlaceholder++;

                if (indexPlaceholder > NUMBER_BIND_VARIABLES_SELECT_SQL_TEXT) {
                    // all place holders are filled - so fetch the sql text
                    // from the db
                    getTextFromDB(sqlTextStatement);

                    indexPlaceholder = 1;
                }
            }

            if (indexPlaceholder > 1) {
                // there are some statements left...
                // fill the empty bind variables with invalid IDs
                for (int index = indexPlaceholder; index <= NUMBER_BIND_VARIABLES_SELECT_SQL_TEXT; index++) {
                    sqlTextStatement.setString(index, "");
                }
                getTextFromDB(sqlTextStatement);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            ConnectionPoolUtils.returnConnectionToPool(connection);
        }
    }

    private static void getTextFromDB(PreparedStatement sqlTextStatement)
            throws SQLException {
        SQLStatement sqlStatement;

        ResultSet sqlTextResultSet = sqlTextStatement.executeQuery();

        // use a definitely not used (invalid) sqlId to get started
        String sqlId = "";
        StringBuffer sqlText = new StringBuffer();
        while (sqlTextResultSet.next()) {
            if (sqlId.equals(sqlTextResultSet.getString(1))) {
                // found next piece of the SQL text
                sqlText.append(sqlTextResultSet.getString(2));
            } else {
                // new sqlId
                // save the SQL text of the prior statement
                sqlStatement = handleSQLStatementWithSqlId(sqlId);
                if (sqlStatement != null) {
                    sqlStatement.setSqlText(sqlText.toString());
                }

                // start gathering the next SQL string
                sqlId = sqlTextResultSet.getString(1);
                sqlText = new StringBuffer(sqlTextResultSet.getString(2));
            }
        }
        // save the SQL text of the prior statement
        sqlStatement = handleSQLStatementWithSqlId(sqlId);
        if (sqlStatement != null) {
            sqlStatement.setSqlText(sqlText.toString());
        }
    }

    private static SQLStatement handleSQLStatementWithSqlId(String sqlId) {
        for (SQLStatement statement : allSQLStatements) {
            if (sqlId.equals(statement.getSqlId())) {
                // we found the statement
                return statement;
            }
        }
        // we didn't find the statement
        return null;
    }

    public static void loadExecutionPlansForStatements(List<DeltaSQLStatementSnapshot> sqlStatements,
                                                       ProgressListener progressListener) {
        if (progressListener != null) {
            progressListener.setStartValue(0);
            progressListener.setEndValue(sqlStatements.size());
            progressListener.setCurrentValue(0);
        }

        int progressCounter = 0;

        List<DeltaSQLStatementSnapshot> missingExecutionPlanSqlStatements = new ArrayList<>(sqlStatements);
        List<DeltaSQLStatementSnapshot> sqlStatementsToLoad = new ArrayList<>();

        int numberStatements;

        while (missingExecutionPlanSqlStatements.size() > 0) {
            sqlStatementsToLoad.clear();
            numberStatements = 0;

            Iterator<DeltaSQLStatementSnapshot> iterator = missingExecutionPlanSqlStatements.iterator();

            while (iterator.hasNext() && numberStatements <= NUMBER_BIND_VARIABLES_SELECT_EXECUTION_PLAN) {
                sqlStatementsToLoad.add(iterator.next());
                numberStatements++;
            }

            // remove those statements from the initial for which the loading is going on
            for (DeltaSQLStatementSnapshot statement : sqlStatementsToLoad) {
                missingExecutionPlanSqlStatements.remove(statement);
            }

            loadExecutionPlansForSQLStatements(sqlStatementsToLoad);

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
    }

    private static void loadExecutionPlansForSQLStatements(List<DeltaSQLStatementSnapshot> sqlStatementsToLoad) {

        Connection connection = null;
        try {
            connection = ConnectionPoolUtils.getConnectionFromPool();
            PreparedStatement sqlExecutionPlansStatement = connection
                    .prepareStatement(SELECT_EXECUTION_PLANS_FOR_100_STATEMENTS);
            sqlExecutionPlansStatement.setFetchSize(1000);

            int indexPlaceholder = 1;
            for (DeltaSQLStatementSnapshot currentStatement : sqlStatementsToLoad) {
                sqlExecutionPlansStatement.setString(indexPlaceholder,
                        currentStatement.getSqlStatement().getSqlId());
                indexPlaceholder++;

                if (indexPlaceholder > NUMBER_BIND_VARIABLES_SELECT_EXECUTION_PLAN) {
                    // all place holders are filled - so fetch the execution
                    // plans from the DB
                    getExecutionPlansFromDB(sqlStatementsToLoad,
                            sqlExecutionPlansStatement);

                    indexPlaceholder = 1;
                }
            }

            if (indexPlaceholder > 1) {
                // there are some statements left...
                // fill the empty bind variables with invalid addresses
                for (int index = indexPlaceholder; index <= NUMBER_BIND_VARIABLES_SELECT_EXECUTION_PLAN; index++) {
                    sqlExecutionPlansStatement.setString(index, "");
                }
                getExecutionPlansFromDB(sqlStatementsToLoad,
                        sqlExecutionPlansStatement);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            ConnectionPoolUtils.returnConnectionToPool(connection);
        }
    }

    private static void getExecutionPlansFromDB(
            List<DeltaSQLStatementSnapshot> sqlStatementsToLoad,
            PreparedStatement sqlTextStatement) throws SQLException {

        ResultSet executionPlansResultSet = sqlTextStatement.executeQuery();

        // use a definitely not used (invalid) SQL ID and ChildId to get started
        String lastSqlId = "";
        int lastInstanceNumber = -1;
        int lastChildNumber = -1;
        ExecutionPlan lastExecutionPlan = null;
        while (executionPlansResultSet.next()) {

            int resultIndex = 1;
            String currentSqlId = executionPlansResultSet.getString(resultIndex++);
            int currentInstanceNumber = executionPlansResultSet.getInt(resultIndex++);
            int currentChildNumber = executionPlansResultSet.getInt(resultIndex++);

            // find the SQL statement with this address
            DeltaSQLStatementSnapshot deltaSQLStatementSnapshot = findSQLStatementWithSqlId(sqlStatementsToLoad,
                    currentSqlId);
            if (deltaSQLStatementSnapshot == null) {
                // there is no SQL statement with this address in the list
                // this should never happen...
                throw new RuntimeException(
                        "Fatal Exception: Oracle returned a result not being asked for.");
            }

            ExecutionPlanStep newStep = new ExecutionPlanStep(
                    // id
                    executionPlansResultSet.getInt(resultIndex++),
                    // parent id
                    executionPlansResultSet.getInt(resultIndex++),
                    // operation
                    executionPlansResultSet.getString(resultIndex++),
                    // options
                    executionPlansResultSet.getString(resultIndex++),
                    // objectOwner
                    executionPlansResultSet.getString(resultIndex++),
                    // objectName
                    executionPlansResultSet.getString(resultIndex++),
                    // depth
                    executionPlansResultSet.getBigDecimal(resultIndex++),
                    // position
                    executionPlansResultSet.getBigDecimal(resultIndex++),
                    // cost
                    executionPlansResultSet.getBigDecimal(resultIndex++),
                    // cardinality
                    executionPlansResultSet.getBigDecimal(resultIndex++),
                    // bytes
                    executionPlansResultSet.getBigDecimal(resultIndex++),
                    // cpuCost
                    executionPlansResultSet.getBigDecimal(resultIndex++),
                    // ioCost
                    executionPlansResultSet.getBigDecimal(resultIndex++),
                    // accessPredicates
                    executionPlansResultSet.getString(resultIndex++),
                    // filterPredicates
                    executionPlansResultSet.getString(resultIndex++));

            if (lastSqlId.equals(currentSqlId)
                    && lastInstanceNumber == currentInstanceNumber
                    && lastChildNumber == currentChildNumber) {
                // a new step for the current child of the current instance of the current SQL statement
                if (!lastExecutionPlan.getParentStep()
                        .insertStepToCorrectPositionInStepOrChilds(newStep)) {
                    // the new step could not be integrated into the execution plan.
                    // this should never happen...
                    throw new RuntimeException(
                            "Fatal Exception: Oracle returned a step of an execution plan that could not be integrated.");
                }
            } else {
                // a new execution plan for the current address/SQL statement or
                // a new address/SQL statement
                // no matter - in both cases we need a new execution plan for
                // the
                // current statement
                lastExecutionPlan = new ExecutionPlan(currentInstanceNumber,
                        currentChildNumber,
                        currentSqlId);
                lastExecutionPlan.setParentStep(newStep);
                deltaSQLStatementSnapshot.getSqlStatement().addExecutionPlan(lastExecutionPlan);
            }

            lastSqlId = currentSqlId;
            lastInstanceNumber = currentInstanceNumber;
            lastChildNumber = currentChildNumber;
        }
    }

    private static DeltaSQLStatementSnapshot findSQLStatementWithSqlId(
            List<DeltaSQLStatementSnapshot> sqlStatements, String sqlId) {
        for (DeltaSQLStatementSnapshot statement : sqlStatements) {
            if (sqlId.equals(statement.getSqlStatement().getSqlId())) {
                return statement;
            }
        }
        // we didn't find the statement
        return null;
    }

    public static int getNumberSQLStatements() {
        int result = 0;

        Connection connection = ConnectionPoolUtils.getConnectionFromPool();
        try {
            PreparedStatement numberStatementsStatement = connection
                    .prepareStatement(NUMBER_STATEMENTS_IN_LIBRARY_CACHE);

            ResultSet numberStatementsResultSet = numberStatementsStatement
                    .executeQuery();

            if (numberStatementsResultSet != null) {
                while (numberStatementsResultSet.next()) {
                    result = numberStatementsResultSet.getInt(1);
                }
            }

            numberStatementsStatement.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            ConnectionPoolUtils.returnConnectionToPool(connection);
        }
        return result;
    }

    public static Calendar getCurrentDBDateTime() {
        Calendar result = null;

        Connection connection = ConnectionPoolUtils.getConnectionFromPool();
        try {
            PreparedStatement dateTimeStatement = connection
                    .prepareStatement(DATE_TIME_DATABASE);

            ResultSet dateTimeResultSet = dateTimeStatement.executeQuery();

            if (dateTimeResultSet != null) {
                Timestamp currentDBTime = null;
                while (dateTimeResultSet.next()) {
                    currentDBTime = dateTimeResultSet.getTimestamp(1);
                }
                result = new GregorianCalendar();
                result.setTimeInMillis(currentDBTime.getTime());
            }

            dateTimeStatement.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            ConnectionPoolUtils.returnConnectionToPool(connection);
        }

        return result;
    }
}
