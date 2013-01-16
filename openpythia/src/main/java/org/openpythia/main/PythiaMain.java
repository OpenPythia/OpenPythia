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
package org.openpythia.main;

import java.util.List;

import javax.swing.UIManager;

import org.openpythia.dbconnection.ConnectionPool;
import org.openpythia.dbconnection.DBConnectionParametersController;
import org.openpythia.dbconnection.JDBCHandler;
import org.openpythia.maindialog.MainDialogController;
import org.openpythia.schemaprivileges.MissingPrivilegesController;
import org.openpythia.schemaprivileges.PrivilegesHelper;

public class PythiaMain {

    /**
     * The method to start the GUI of Pythia - the analyzer for the Oracle DB.
     * 
     * @param args
     *            The default parameters for main methods. The parameters are
     *            ignored.
     */
    public static void main(String[] args) {

        switchLookAndFeel();

        if (!JDBCHandler.makeJDBCDriverAvailable()) {
            // Pythia can not run without an appropriate JDBC driver
            gracefullExit();
        }

        if (!DBConnectionParametersController.establishDBConnection()) {
            // When being asked for the connection details the user pressed the
            // cancel button
            gracefullExit();
        }

        checkPrivileges(DBConnectionParametersController.getConnectionPool());

        openMainDialog(DBConnectionParametersController.getConnectionPool());
    }

    private static void switchLookAndFeel() {
        try {
            // The default look & feel in Windows looks just ugly - so switch to
            // a nicer look & feel
            if (System.getProperties().getProperty("os.name")
                    .contains("Windows")) {
                UIManager
                        .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
        } catch (Exception e) {
            // Just ignore any exception: There will be no problem running with
            // the default look & feel.
        }
    }

    private static void checkPrivileges(ConnectionPool connectionPool) {
        List<String> missingObjectPrivileges = PrivilegesHelper
                .getMissingObjectPrivileges(connectionPool);

        if (missingObjectPrivileges.size() > 0) {
            new MissingPrivilegesController(PrivilegesHelper.createGrantScript(
                    missingObjectPrivileges, connectionPool.getSchemaName()));
        }
    }

    private static void openMainDialog(ConnectionPool connectionPool) {
        new MainDialogController(connectionPool);
    }

    public static void gracefullExit() {
        if (DBConnectionParametersController.getConnectionPool() != null) {
            DBConnectionParametersController.getConnectionPool()
                    .releaseAllPooledConnections();
        }
        System.exit(0);
    }
}
