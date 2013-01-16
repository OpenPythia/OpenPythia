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

/**
 * Interface for a connection pool. All the connections handled by this pool are
 * related to one schema at one DB. Pythia doesn't need any more.
 */
public interface ConnectionPool {

    /**
     * @return The host of the database the connection pool is connected to.
     */
    String getHost();

    /**
     * @return The port of the database the connection pool is connected to.
     */
    String getPort();

    /**
     * @return The name database the connection pool is connected to.
     */
    String getDatabaseName();

    /**
     * @return The name of the user / schema the connection pool is connected
     *         to.
     */
    String getSchemaName();

    /**
     * Get a connection to the database this pool is connected to.
     * 
     * The implementation of this method returns a connection - without making
     * any promises how long it takes. If the connection pool is empty, this
     * method blocks till a connection is given back.
     * 
     * @return a connection.
     */
    Connection getConnection();

    /**
     * Give a connection back / release the connection. When a connection is no
     * longer needed it can be given back via this method.
     * 
     * @param connection
     *            The connection that should be given back.
     */
    void giveConnectionBack(Connection connection);

    /**
     * The connection pool releases all connection - no matter if they are idle
     * or are used. This method must only be called when shutting the
     * application down.
     */
    void releaseAllPooledConnections();
}