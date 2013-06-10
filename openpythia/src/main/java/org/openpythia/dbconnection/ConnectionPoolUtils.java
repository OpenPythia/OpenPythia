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

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class provides a convenience interface for Apache DBCP to retrieve connections
 * from the configurePool pool and to shutdown the pool.
 */
public class ConnectionPoolUtils {

    private static final String POOL_NAME = "pythia";
    private static final String POOL_DRIVER_URL = "jdbc:apache:commons:dbcp:";
    private static final String POOLING_DRIVER = "org.apache.commons.dbcp.PoolingDriver";

    private static String userName;

    private static boolean hasBeenSuccessfullyConfigured = false;

    private ConnectionPoolUtils() {
    }

    /**
     * Configures an Apache DBCP pool called POOL_NAME. For the given connectionUrl
     * and connectionProperties.
     *
     * @param connectionUrl the connection url
     * @param connectionProperties  the connectionProperties
     */
    public static void configurePool(String connectionUrl, Properties connectionProperties) throws SQLException {
        try {
            ConnectionFactory connectionFactory = new DriverConnectionFactory(JDBCHandler.getOracleJDBCDriver(),
                    connectionUrl, connectionProperties);

            userName = connectionProperties.getProperty("user");

            GenericObjectPool connectionPool = new GenericObjectPool();
            connectionPool.setFactory(new PoolableConnectionFactory(connectionFactory, connectionPool,
                    null, null, false, true));

            Class.forName(POOLING_DRIVER);

            PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(POOL_DRIVER_URL);
            driver.registerPool(POOL_NAME, connectionPool);

            Connection connection = DriverManager.getConnection(POOL_DRIVER_URL +POOL_NAME);
            connection.close();

            hasBeenSuccessfullyConfigured = true;
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Returns a connection from the previous configurePool the pool
     * @return  the connection
     */
    public static Connection getConnectionFromPool() {
        try {
            return DriverManager.getConnection(POOL_DRIVER_URL +POOL_NAME);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shuts the configurePool poll down and releases all resources
     */
    public static void shutdownPool() {
        try {
            if(hasBeenSuccessfullyConfigured) {
                PoolingDriver poolingDriver = (PoolingDriver)DriverManager.getDriver(POOL_DRIVER_URL);
                poolingDriver.closePool(POOL_NAME);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the given connection to its originating pool
     * @param connection  the connection to return
     */
    public static void returnConnectionToPool(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLoggedInUserName() {
        return userName;
    }
}
