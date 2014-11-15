package org.openpythia.batch;

import org.openpythia.dbconnection.ConnectionPoolUtils;

import java.sql.SQLException;
import java.util.Properties;

public class BatchHelper {

    private static String getConnectionString(DBConnectionInformation dbConnectionInformation) {
        String connectionString;
        if (dbConnectionInformation.getSid() != null) {
            connectionString = String.format("jdbc:oracle:thin:@%s:%d:%s",
                    dbConnectionInformation.getHost(),
                    Integer.parseInt(dbConnectionInformation.getPort()),
                    dbConnectionInformation.getSid());
        } else if (dbConnectionInformation.getServiceName() != null) {
            connectionString = String.format("jdbc:oracle:thin:@%s:%d/%s",
                    dbConnectionInformation.getHost(),
                    Integer.parseInt(dbConnectionInformation.getPort()),
                    dbConnectionInformation.getServiceName());
        } else {
            connectionString = String.format("jdbc:oracle:thin:@%s:%d/%s", dbConnectionInformation.getHost(),
                    Integer.parseInt(dbConnectionInformation.getPort()), dbConnectionInformation.getTnsName());
        }
        return connectionString;
    }

    public static boolean initializeConnectionPool(DBConnectionInformation dbConnectionInformation) {
        Properties credentials = new Properties();
        credentials.put("user", dbConnectionInformation.getUser());
        credentials.put("password", dbConnectionInformation.getPassword());

        try {
            ConnectionPoolUtils.configurePool(BatchHelper.getConnectionString(dbConnectionInformation), credentials);
        } catch (SQLException e) {
            // The connection could not be established
            System.out.println("The connection could not be established. The error message is \n" +
                    e.toString());
            return false;
        }

        return true;
    }
}
