package org.openpythia.utilities.sql;

import org.openpythia.dbconnection.ConnectionPoolUtils;
import org.openpythia.progress.ProgressListener;
import org.openpythia.utilities.deltasql.DeltaSQLStatementSnapshot;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLExecutionPlanLoadHelper {

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

    protected static void loadExecutionPlansForStatements(List<DeltaSQLStatementSnapshot> sqlStatements,
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
            DeltaSQLStatementSnapshot deltaSQLStatementSnapshot =
                    SQLHelper.findSQLStatementWithSqlId(sqlStatementsToLoad, currentSqlId);
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
                // a new execution plan for the current address/SQL statement or a new address/SQL statement
                // no matter - in both cases we need a new execution plan for the current statement
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
}
