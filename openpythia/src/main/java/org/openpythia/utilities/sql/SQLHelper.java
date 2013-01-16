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

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;

import org.openpythia.dbconnection.ConnectionPool;
import org.openpythia.progress.ProgressListener;

public class SQLHelper {

    private static String NUMBER_STATEMENTS_IN_LIBRARY_CACHE = "SELECT COUNT(*) "
            + "FROM v$sqlarea";

    private final static String DATE_TIME_DATABASE = "SELECT sysdate "
            + "FROM dual";

    private final static String SELECT_SQL_TEXT_FOR_ONE_STATEMENT = "SELECT address, sql_text "
            + "FROM v$sqltext_with_newlines "
            + "WHERE address = ?"
            + "ORDER BY address, piece";

    private final static int NUMBER_BIND_VARIABLES = 100;
    private final static String SELECT_SQL_TEXT_FOR_100_STATEMENTS = "SELECT address, sql_text "
            + "FROM v$sqltext_with_newlines "
            + "WHERE address IN ("
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " + "ORDER BY address, piece";

    private final static String SELECT_EXECUTION_PLANS_FOR_100_STATEMENTS = "SELECT address, child_number, id, parent_id, operation, "
            + "options, object_owner, object_name, depth, position, cost, cardinality, "
            + "bytes, cpu_cost, io_cost, access_predicates, filter_predicates "
            + "FROM v$sql_plan "
            + "WHERE address IN ("
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
            + "ORDER BY address, child_number, id, position";

    private static List<SQLStatement> allSQLStatements = new ArrayList<SQLStatement>();
    private static List<SQLStatement> unloadedSQLStatements = new CopyOnWriteArrayList<SQLStatement>();

    private static SQLStatementLoader sqlStatementLoader;

    public static SQLStatement getSQLStatement(String sqlId, String address,
            String parsingSchema) {
        SQLStatement result = null;

        SQLStatement newStatement = new SQLStatement(sqlId, address,
                parsingSchema);

        if (allSQLStatements.contains(newStatement)) {
            // reuse of an existing statement
            result = allSQLStatements.get(allSQLStatements
                    .indexOf(newStatement));
        } else {
            allSQLStatements.add(newStatement);
            unloadedSQLStatements.add(newStatement);
            result = newStatement;
        }

        return result;
    }

    public static void loadSQLTextForStatements(ConnectionPool connectionPool,
            List<SQLStatement> sqlStatements, ProgressListener progressListener) {
        if (progressListener != null) {
            progressListener.setStartValue(0);
            progressListener.setEndValue(sqlStatements.size());
            progressListener.setCurrentValue(0);
        }

        int progressCounter = 0;
        for (SQLStatement statement : sqlStatements) {
            loadSQLTextForStatement(connectionPool, statement);

            progressCounter++;
            if (progressListener != null) {
                progressListener.setCurrentValue(progressCounter);
            }
        }
        if (progressListener != null) {
            progressListener.informFinished();
        }
    }

    public static void loadSQLTextForStatement(ConnectionPool connectionPool,
            SQLStatement sqlStatement) {

        if (sqlStatement.getSqlText() == null) {
            // statement has no sql text assigned - so load it...

            Connection connection = null;
            PreparedStatement sqlTextStatement = null;
            try {
                connection = connectionPool.getConnection();
                sqlTextStatement = connection
                        .prepareStatement(SELECT_SQL_TEXT_FOR_ONE_STATEMENT);
                sqlTextStatement.setString(1, sqlStatement.getAddress());

                ResultSet sqlTextResultSet = sqlTextStatement.executeQuery();

                StringBuffer sqlText = new StringBuffer();
                while (sqlTextResultSet.next()) {
                    sqlText.append(sqlTextResultSet.getString(2));
                }
                sqlStatement.setSqlText(sqlText.toString());

                unloadedSQLStatements.remove(sqlStatement);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog((Component) null, e);
            } finally {
                if (sqlTextStatement != null) {
                    try {
                        sqlTextStatement.close();
                    } catch (SQLException e) {
                        // ignore
                    }
                }
                connectionPool.giveConnectionBack(connection);
            }
        }
    }

    public static synchronized void startSQLTextLoader(
            ConnectionPool connectionPool) {
        if (sqlStatementLoader == null) {
            sqlStatementLoader = new SQLStatementLoader(connectionPool);
            new Thread(sqlStatementLoader).start();
        }
    }

    private static class SQLStatementLoader implements Runnable {

        private ConnectionPool connectionPool;

        public SQLStatementLoader(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
        }

        @Override
        public void run() {
            List<SQLStatement> sqlStatementsToLoad = new ArrayList<SQLStatement>();
            int numberStatements;

            while (true) {
                if (unloadedSQLStatements.size() > 0) {

                    sqlStatementsToLoad.clear();
                    numberStatements = 0;

                    Iterator<SQLStatement> iterator = unloadedSQLStatements
                            .iterator();

                    while (iterator.hasNext() && numberStatements <= 200) {
                        sqlStatementsToLoad.add(iterator.next());
                        numberStatements++;
                    }

                    loadSQLStatements(sqlStatementsToLoad);
                }

                // sleep for 0.2 seconds so all the other tasks can do their
                // work
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // we don't care for being interrupted
                }
            }
        }

        private void loadSQLStatements(List<SQLStatement> sqlStatementsToLoad) {

            Connection connection = null;
            try {
                connection = connectionPool.getConnection();
                PreparedStatement sqlTextStatement = connection
                        .prepareStatement(SELECT_SQL_TEXT_FOR_100_STATEMENTS);
                sqlTextStatement.setFetchSize(1000);

                int indexPlaceholder = 1;
                for (SQLStatement currentStatement : sqlStatementsToLoad) {
                    sqlTextStatement.setString(indexPlaceholder,
                            currentStatement.getAddress());
                    indexPlaceholder++;

                    if (indexPlaceholder > NUMBER_BIND_VARIABLES) {
                        // all place holders are filled - so fetch the sql text
                        // from the db
                        getTextFromDB(sqlTextStatement);

                        indexPlaceholder = 1;
                    }
                }

                if (indexPlaceholder > 1) {
                    // there are some statements left...
                    // fill the empty bind variables with invalid IDs
                    for (int index = indexPlaceholder; index <= NUMBER_BIND_VARIABLES; index++) {
                        sqlTextStatement.setString(index, "");
                    }
                    getTextFromDB(sqlTextStatement);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog((Component) null, e);
            } finally {
                connectionPool.giveConnectionBack(connection);
            }
        }

        private void getTextFromDB(PreparedStatement sqlTextStatement)
                throws SQLException {
            SQLStatement sqlStatement;

            ResultSet sqlTextResultSet = sqlTextStatement.executeQuery();

            // use a definitely not used (invalid) address to get started
            String address = "";
            StringBuffer sqlText = new StringBuffer();
            while (sqlTextResultSet.next()) {
                if (address.equals(sqlTextResultSet.getString(1))) {
                    // found next piece of the SQL text
                    sqlText.append(sqlTextResultSet.getString(2));
                } else {
                    // new address
                    // save the SQL text of the prior statement
                    sqlStatement = handleSQLStatementWithAddress(address);
                    if (sqlStatement != null) {
                        sqlStatement.setSqlText(sqlText.toString());
                    }

                    // start gathering the next SQL string
                    address = sqlTextResultSet.getString(1);
                    sqlText = new StringBuffer(sqlTextResultSet.getString(2));
                }
            }
            // save the SQL text of the prior statement
            sqlStatement = handleSQLStatementWithAddress(address);
            if (sqlStatement != null) {
                sqlStatement.setSqlText(sqlText.toString());
            }
        }

        private SQLStatement handleSQLStatementWithAddress(String address) {
            for (SQLStatement statement : unloadedSQLStatements) {
                if (address.equals(statement.getAddress())) {
                    // we found the statement
                    unloadedSQLStatements.remove(statement);
                    return statement;
                }
            }
            // we didn't find the statement
            return null;
        }
    }

    public static void loadExecutionPlansForStatements(
            ConnectionPool connectionPool, List<SQLStatement> sqlStatements,
            ProgressListener progressListener) {
        if (progressListener != null) {
            progressListener.setStartValue(0);
            progressListener.setEndValue(sqlStatements.size());
            progressListener.setCurrentValue(0);
        }

        int progressCounter = 0;

        List<SQLStatement> missingExecutionPlanSqlStatements = sqlStatements;
        List<SQLStatement> sqlStatementsToLoad = new ArrayList<SQLStatement>();

        int numberStatements;

        while (missingExecutionPlanSqlStatements.size() > 0) {
            sqlStatementsToLoad.clear();
            numberStatements = 0;

            Iterator<SQLStatement> iterator = missingExecutionPlanSqlStatements
                    .iterator();

            // add up to 200 statements on the lict of statements, for which the
            // execution plan will be loaded
            while (iterator.hasNext() && numberStatements <= 200) {
                sqlStatementsToLoad.add(iterator.next());
                numberStatements++;
            }

            // remove those statements from the initial for which the loading is
            // going on
            for (SQLStatement statement : sqlStatementsToLoad) {
                missingExecutionPlanSqlStatements.remove(statement);
            }

            loadExecutionPlansForSQLStatements(connectionPool,
                    sqlStatementsToLoad);

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

    private static void loadExecutionPlansForSQLStatements(
            ConnectionPool connectionPool,
            List<SQLStatement> sqlStatementsToLoad) {

        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            PreparedStatement sqlExecutionPlansStatement = connection
                    .prepareStatement(SELECT_EXECUTION_PLANS_FOR_100_STATEMENTS);
            sqlExecutionPlansStatement.setFetchSize(1000);

            int indexPlaceholder = 1;
            for (SQLStatement currentStatement : sqlStatementsToLoad) {
                sqlExecutionPlansStatement.setString(indexPlaceholder,
                        currentStatement.getAddress());
                indexPlaceholder++;

                if (indexPlaceholder > NUMBER_BIND_VARIABLES) {
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
                for (int index = indexPlaceholder; index <= NUMBER_BIND_VARIABLES; index++) {
                    sqlExecutionPlansStatement.setString(index, "");
                }
                getExecutionPlansFromDB(sqlStatementsToLoad,
                        sqlExecutionPlansStatement);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } finally {
            connectionPool.giveConnectionBack(connection);
        }
    }

    private static void getExecutionPlansFromDB(
            List<SQLStatement> sqlStatementsToLoad,
            PreparedStatement sqlTextStatement) throws SQLException {
        SQLStatement sqlStatement;

        ResultSet executionPlansResultSet = sqlTextStatement.executeQuery();

        // use a definitely not used (invalid) address and ChildId to get
        // started
        String lastAddress = "";
        int lastChildNumber = -1;
        ExecutionPlan lastExecutionPlan = null;
        while (executionPlansResultSet.next()) {

            String currentAddress = executionPlansResultSet.getString(1);
            int currentChildNumber = executionPlansResultSet.getInt(2);

            // find the SQL statement with this address
            sqlStatement = findSQLStatementWithAddress(sqlStatementsToLoad,
                    currentAddress);
            if (sqlStatement == null) {
                // there is no SQL statement with this address in the list
                // this should never happen...
                throw new RuntimeException(
                        "Fatal Exception: Oracle returned a result not beeing asked for.");
            }

            ExecutionPlanStep newStep = new ExecutionPlanStep(
            // id
                    executionPlansResultSet.getInt(3),
                    // parent id
                    executionPlansResultSet.getInt(4),
                    // operation
                    executionPlansResultSet.getString(5),
                    // options
                    executionPlansResultSet.getString(6),
                    // objectOwner
                    executionPlansResultSet.getString(7),
                    // objectName
                    executionPlansResultSet.getString(8),
                    // depth
                    executionPlansResultSet.getInt(9),
                    // position
                    executionPlansResultSet.getInt(10),
                    // cost
                    executionPlansResultSet.getInt(11),
                    // cardinality
                    executionPlansResultSet.getInt(12),
                    // bytes
                    executionPlansResultSet.getInt(13),
                    // cpuCost
                    executionPlansResultSet.getInt(14),
                    // ioCost
                    executionPlansResultSet.getInt(15),
                    // accessPredicates
                    executionPlansResultSet.getString(16),
                    // filterPredicates
                    executionPlansResultSet.getString(17));

            if (lastAddress.equals(currentAddress)
                    && lastChildNumber == currentChildNumber) {
                // a new step for the current child of the current address/SQL
                // statement
                if (!lastExecutionPlan.getParentStep()
                        .insertStepToCorrectionPositionInStepOrChilds(newStep)) {
                    // the new step could not be integrated into the execution
                    // plan.
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
                lastExecutionPlan = new ExecutionPlan(currentChildNumber,
                        currentAddress);
                lastExecutionPlan.setParentStep(newStep);
                sqlStatement.addExecutionPlan(lastExecutionPlan);
            }

            lastAddress = currentAddress;
            lastChildNumber = currentChildNumber;
        }
    }

    private static SQLStatement findSQLStatementWithAddress(
            List<SQLStatement> sqlStatements, String address) {
        for (SQLStatement statement : sqlStatements) {
            if (address.equals(statement.getAddress())) {
                return statement;
            }
        }
        // we didn't find the statement
        return null;
    }

    public static int getNumberSQLStatements(ConnectionPool connectionPool) {
        int result = 0;

        Connection connection = connectionPool.getConnection();
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
            JOptionPane.showMessageDialog((Component) null, e);
        } finally {
            connectionPool.giveConnectionBack(connection);
        }
        return result;
    }

    public static Date getCurrentDBDateTime(ConnectionPool connectionPool) {
        Date result = null;

        Connection connection = connectionPool.getConnection();
        try {
            PreparedStatement dateTimeStatement = connection
                    .prepareStatement(DATE_TIME_DATABASE);

            ResultSet dateTimeResultSet = dateTimeStatement.executeQuery();

            if (dateTimeResultSet != null) {
                while (dateTimeResultSet.next()) {
                    java.sql.Date currentDBDate = dateTimeResultSet.getDate(1);
                    java.sql.Time currentDBTime = dateTimeResultSet.getTime(1);
                    result = new Date(currentDBDate.getTime()
                            + currentDBTime.getTime());
                }
            }

            dateTimeStatement.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } finally {
            connectionPool.giveConnectionBack(connection);
        }
        return result;
    }
}
