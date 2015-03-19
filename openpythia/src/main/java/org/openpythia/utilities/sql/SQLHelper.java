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
import org.openpythia.utilities.waitevent.WaitEventForStatementTuple;
import org.openpythia.utilities.waitevent.WaitEventForTimeSpanTuple;

import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SQLHelper {

    private final static String NUMBER_STATEMENTS_IN_LIBRARY_CACHE = "SELECT COUNT(*) "
            + "FROM gv$sqlarea";

    private final static String DATE_TIME_DATABASE = "SELECT sysdate "
            + "FROM dual";

    private static SQLStatementLoader sqlStatementLoader;

    private static List<SQLStatement> allSQLStatements = new CopyOnWriteArrayList<>();
    private static List<SQLStatement> unloadedSQLStatements = new CopyOnWriteArrayList<>();

    public static SQLStatement getSQLStatement(String sqlId, String parsingSchema, int instance, String sqlText) {
        SQLStatement result;

        SQLStatement newStatement = new SQLStatement(sqlId, parsingSchema, instance);

        if (allSQLStatements.contains(newStatement)) {
            // reuse of an existing statement
            result = allSQLStatements.get(allSQLStatements.indexOf(newStatement));
        } else {
            allSQLStatements.add(newStatement);
            if (sqlText != null && !sqlText.equals("")) {
                newStatement.setSqlText(sqlText);
            } else {
                unloadedSQLStatements.add(newStatement);
            }
            result = newStatement;
        }

        return result;
    }

    public static SQLStatement getSQLStatement(String sqlId, String parsingSchema, int instance) {
        return getSQLStatement(sqlId, parsingSchema, instance, null);
    }

    public static SQLStatement getRegisterSQLStatement(SQLStatement sqlStatement) {
        SQLStatement result = getSQLStatement(
                sqlStatement.getSqlId(),
                sqlStatement.getParsingSchema(),
                sqlStatement.getInstanceId());

        if (sqlStatement.getSqlText() != null) {
            result.setSqlText(sqlStatement.getSqlText());
        }

        return result;
    }

    public static void waitForAllSQLTextToBeLoaded(ProgressListener listener) {
        SQLTextLoadHelper.loadSQLTextForStatements(unloadedSQLStatements, listener);
    }

    public static void loadSQLTextForStatements(List<SQLStatement> sqlStatements, ProgressListener progressListener) {
        SQLTextLoadHelper.loadSQLTextForStatements(sqlStatements, progressListener);
    }

    public static void loadExecutionPlansForStatements(List<DeltaSQLStatementSnapshot> sqlStatements,
                                                       ProgressListener progressListener) {
        SQLExecutionPlanLoadHelper.loadExecutionPlansForStatements(sqlStatements, progressListener);
    }

    public static Map<DeltaSQLStatementSnapshot, List<WaitEventForStatementTuple>> loadWaitEventsForStatements(
            List<DeltaSQLStatementSnapshot> worstStatements,
            Calendar startTime,
            Calendar stopTime,
            ProgressListener progressListener) {

        return SQLWaitLoadHelper.loadWaitEventsForStatements(
                worstStatements,
                startTime,
                stopTime,
                progressListener);
    }

    public static List<WaitEventForTimeSpanTuple> loadWaitEventsForTimeSpan(Calendar startTime,
                                                                               Calendar stopTime) {

        return SQLWaitLoadHelper.loadWaitEventsForTimeSpan(startTime, stopTime);
    }


    public static int getNumberSQLStatementsInLibraryCache() {
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

    public static synchronized void startSQLTextLoader() {
        if (sqlStatementLoader == null) {
            sqlStatementLoader = new SQLStatementLoader();
            new Thread(sqlStatementLoader).start();
        }
    }

    private static class SQLStatementLoader implements Runnable {

        public void run() {
            List<SQLStatement> sqlStatementsToLoad = new ArrayList<>();
            int numberStatements;

            //noinspection InfiniteLoopStatement
            while (true) {
                if (unloadedSQLStatements.size() > 0) {

                    sqlStatementsToLoad.clear();
                    numberStatements = 0;

                    Iterator<SQLStatement> iterator = unloadedSQLStatements.iterator();

                    // load at most 400 statements before going to sleep
                    // 400 is a magic number - no special meaning. But keep it somehow related to the
                    // SQLTextLoadHelper and its chunk size.
                    while (iterator.hasNext() && numberStatements <= 400) {
                        sqlStatementsToLoad.add(iterator.next());
                        numberStatements++;
                    }

                    SQLTextLoadHelper.loadSQLStatements(sqlStatementsToLoad);

                    // remove the loaded statements from the list
                    for (SQLStatement statement : sqlStatementsToLoad) {
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

    protected static DeltaSQLStatementSnapshot findSQLStatementWithSqlId(
            List<DeltaSQLStatementSnapshot> sqlStatements, String sqlId) {

        for (DeltaSQLStatementSnapshot statement : sqlStatements) {
            if (sqlId.equals(statement.getSqlStatement().getSqlId())) {
                return statement;
            }
        }
        // we didn't find the statement
        return null;
    }

    protected static void setTextForSQLStatementWithSqlId(String sqlId, String sqlText) {

        for (SQLStatement statement : allSQLStatements) {
            if (sqlId.equals(statement.getSqlId())) {
                // we found an instance of the statement
                statement.setSqlText(sqlText);
            }
        }
    }
}
