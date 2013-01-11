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