package org.openpythia.dbconnection;

import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

import javax.swing.JOptionPane;

import org.openpythia.utilities.PreferencesHandler;

public class JDBCHandler {
    final static String JDBC_DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
    private static Driver oracleJDBCDriver = null;

    /**
     * Make the Oracle JDBC driver available - or return that it couldn't be
     * made available.
     * 
     * This method tries to get the JDBC driver by different way: First it looks
     * in the class path. If it couldn't load it from there it tries to load it
     * from a file provided by the user. Maybe the user hasn't provided a file
     * yet - or the file is no longer available. In this case the user is asked
     * for the file.
     * 
     * @return True if the Oracle JDBC driver is available now. False if the
     *         JDBC driver could not be loaded.
     */
    public static boolean makeJDBCDriverAvailable() {
        if (loadJDBCDriverFromClassPath()) {
            return true;
        }

        if (PreferencesHandler.getJDBCDriverFileName() != null) {
            if (loadJDBCDriverFromFile(PreferencesHandler
                    .getJDBCDriverFileName())) {
                // A path for the jar file is in the preferences AND the driver
                // was successfully loaded.
                return true;
            } else {
                PreferencesHandler.setJDBCDriverFileName(null);
            }
        }

        while (PreferencesHandler.getJDBCDriverFileName() == null) {
            // There is no JDBC driver in the class path and we don't know where
            // to load it from - so ask the user...
            MissingJDBCDriverController controller = new MissingJDBCDriverController();

            if (controller.getPathJDBCDriver() == null) {
                // User didn't provide a file -> so they must have selected
                // Cancel
                return false;
            } else if (loadJDBCDriverFromFile(controller.getPathJDBCDriver())) {
                // The file provided by the user contains a valid JDBC driver
                PreferencesHandler.setJDBCDriverFileName(controller
                        .getPathJDBCDriver());
                return true;
            } else {
                // The file provided by the user contains NOT a valid JDBC
                // driver
                JOptionPane
                        .showMessageDialog((Component) null,
                                "The file you provided does not contain a valid JDBC driver from Oracle.");
            }
        }
        
        // we should never get here...
        return false;
    }

    /**
     * Get the Oracle JDBC driver. This method should be called after having
     * called the method makeJDBCDriverAvailable(). Elsewise the method will
     * return null.
     * 
     * @return The Oracle JDBC driver - if there is any available.
     */
    public static Driver getOracleJDBCDriver() {
        return oracleJDBCDriver;
    }

    private static boolean loadJDBCDriverFromClassPath() {
        try {
            Class<?> driverClass = Class.forName(JDBC_DRIVER_CLASS_NAME);
            oracleJDBCDriver = (Driver) driverClass.newInstance();
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * Try to load the Oracle JDBC driver from the given file.
     * 
     * @param driverJARFileName
     *            The file name of the file to load from.
     * @return True if the driver was loaded successfully. False if the driver
     *         was not loaded.
     */
    private static boolean loadJDBCDriverFromFile(String driverJARFileName) {
        File driverJARFile = new File(driverJARFileName);

        URL oracleJDBCDriverURL = null;
        try {
            oracleJDBCDriverURL = driverJARFile.toURI().toURL();
        } catch (MalformedURLException ex) {
            return false;
        }

        URLClassLoader classLoader = new URLClassLoader(
                new URL[] { oracleJDBCDriverURL });
        Class<?> driverClass;
        try {
            driverClass = Class.forName(JDBC_DRIVER_CLASS_NAME, true,
                    classLoader);
            oracleJDBCDriver = (Driver) driverClass.newInstance();
        } catch (ClassNotFoundException e) {
            return false;
        } catch (InstantiationException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }
        return true;
    }

}
