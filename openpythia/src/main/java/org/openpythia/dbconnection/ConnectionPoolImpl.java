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
package org.openpythia.dbconnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPoolImpl implements ConnectionPool {

    /**
     * Number of connections opened and maintained by this implementation.
     */
    private static int NUMBER_CONNECTIONS = 3;

    private String host;
    private String port;
    private String databaseName;
    private String schemaName;

    private BlockingQueue<Connection> freeConnections;
    private BlockingQueue<Connection> releasedConnections;

    public ConnectionPoolImpl(String host, String port, String databaseName,
            String schemaName, char[] password) throws SQLException {

        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.schemaName = schemaName;

        freeConnections = new ArrayBlockingQueue<Connection>(NUMBER_CONNECTIONS);
        releasedConnections = new ArrayBlockingQueue<Connection>(
                NUMBER_CONNECTIONS);

        String connectionString = "jdbc:oracle:thin:@" + host + ":" + port
                + ":" + databaseName;

        Properties credentials = new Properties();
        credentials.setProperty("user", schemaName);
        credentials.setProperty("password", new String(password));

        for (int count = 0; count < NUMBER_CONNECTIONS; count++) {
            // create a new connection
            if (JDBCHandler.getOracleJDBCDriver() == null) {
                throw new RuntimeException(
                        "Undefinded condition: No Oracle JDBC driver available when trying to connect to database.");
            }

            Connection newConnection = JDBCHandler.getOracleJDBCDriver()
                    .connect(connectionString, credentials);

            // make sure we don't write to the database
            newConnection.setReadOnly(true);

            freeConnections.add(newConnection);
        }
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public Connection getConnection() {
        Connection result = null;
        boolean putten = false;

        while (result == null) {
            try {
                result = freeConnections.take();
            } catch (InterruptedException e) {
                // ignore and try again
            }
        }

        while (!putten) {
            try {
                releasedConnections.put(result);
                putten = true;
            } catch (InterruptedException e) {
                // ignore and try again
            }
        }

        return result;
    }

    @Override
    public void giveConnectionBack(Connection connection) {

        if (connection == null) {
            return;
        }

        boolean putten = false;

        // Check if the given connection is one of our connections
        if (releasedConnections.contains(connection)) {
            while (!putten) {
                try {
                    freeConnections.put(connection);
                    putten = true;
                } catch (InterruptedException e) {
                    // ignore and try again
                }
            }
            releasedConnections.remove(connection);
        }
    }

    @Override
    public void releaseAllPooledConnections() {
        for (Connection connection : freeConnections) {
            freeConnections.remove(connection);
            try {
                connection.close();
            } catch (SQLException e) {
                // when shutting down we don't care for problems closing
                // connections
            }
        }

        for (Connection connection : releasedConnections) {
            releasedConnections.remove(connection);
            try {
                connection.close();
            } catch (SQLException e) {
                // when shutting down we don't care for problems closing
                // connections
            }
        }
    }
}
