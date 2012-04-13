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

    // FIXME there should be a more elegant way to achieve this...
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
