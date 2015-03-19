package org.openpythia.utilities.sql;

import org.openpythia.dbconnection.ConnectionPoolUtils;
import org.openpythia.progress.ProgressListener;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SQLTextLoadHelper {

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
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            + "ORDER BY sql_id, piece";

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

    public static void loadSQLStatements(List<SQLStatement> sqlStatementsToLoad) {

        Connection connection = null;
        try {
            connection = ConnectionPoolUtils.getConnectionFromPool();
            if (connection == null) {
                // just for testability: during normal execution we will never get here. But the unit tests are
                // running without a database
                return;
            }

            PreparedStatement sqlTextStatement = connection.prepareStatement(SELECT_SQL_TEXT_FOR_200_STATEMENTS);
            sqlTextStatement.setFetchSize(1000);

            int indexPlaceholder = 1;
            for (SQLStatement currentStatement : sqlStatementsToLoad) {
                sqlTextStatement.setString(indexPlaceholder, currentStatement.getSqlId());
                indexPlaceholder++;

                if (indexPlaceholder > NUMBER_BIND_VARIABLES_SELECT_SQL_TEXT) {
                    // all place holders are filled - so fetch the sql text from the db
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

    private static void getTextFromDB(PreparedStatement sqlTextStatement) throws SQLException {
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
                SQLHelper.setTextForSQLStatementWithSqlId(sqlId, sqlText.toString());

                // start gathering the next SQL string
                sqlId = sqlTextResultSet.getString(1);
                sqlText = new StringBuffer(sqlTextResultSet.getString(2));
            }
        }
        // save the SQL text of the prior statement
        SQLHelper.setTextForSQLStatementWithSqlId(sqlId, sqlText.toString());
    }
}
