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

import org.openpythia.batch.BatchTakeSnapshot;
import org.openpythia.batch.DBConnectionInformation;
import org.openpythia.dbconnection.ConnectionPoolUtils;
import org.openpythia.dbconnection.JDBCHandler;
import org.openpythia.dbconnection.LoginController;
import org.openpythia.maindialog.MainDialogController;
import org.openpythia.preferences.PreferencesManager;
import org.openpythia.schemaprivileges.MissingPrivilegesController;
import org.openpythia.schemaprivileges.PrivilegesHelper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.openpythia.dbconnection.LoginController.LoginResult.CANCEL;

public class PythiaMain {

    /**
     * The method to start the GUI of Pythia - the analyzer for the Oracle DB.
     * 
     * @param args
     *            The default parameters for main methods. The parameters are
     *            ignored.
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            startOpenPythiaInGuiMode();
            return;
        }

        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        if (arguments.contains("-t") || arguments.contains("-T")) {
            printHelpToOutput();
            return;
        }

        if (arguments.contains("-s") || arguments.contains("-S")) {
            startBatchModeTakeSnapshot(arguments);
            return;
        }

        if (arguments.contains("-c")) {
            PreferencesManager.setPathToJDBCDriver("xxx");
            PreferencesManager.savePythiaConfiguration();
            return;
        }

        System.out.println("Unknown options.");
    }

    private static void startOpenPythiaInGuiMode() {
        switchLookAndFeel();

        if (!JDBCHandler.makeJDBCDriverAvailable()) {
            // Pythia can not run without an appropriate JDBC driver
            gracefulExit();
        }

        LoginController loginController = new LoginController();

        if (loginController.showDialog() == CANCEL) {
            // When being asked for the connection details the user pressed the
            // cancel button
            gracefulExit();
        }

        checkPrivileges();

        openMainDialog(loginController.getConnectionName());
    }

    private static void switchLookAndFeel() {
        try {
            // The default look & feel in Windows looks just ugly - so switch to
            // a nicer look & feel
            if (System.getProperties().getProperty("os.name").contains("Windows")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
        } catch (Exception e) {
            // Just ignore any exception: There will be no problem running with
            // the default look & feel.
        }
    }

    private static void checkPrivileges() {
        List<String> missingObjectPrivileges = PrivilegesHelper.getMissingObjectPrivileges();

        if (missingObjectPrivileges.size() > 0) {
            String userName = ConnectionPoolUtils.getLoggedInUserName();

            new MissingPrivilegesController(PrivilegesHelper.createGrantScript(
                    missingObjectPrivileges, userName));
        }
    }

    private static void openMainDialog(String connectionName) {
        new MainDialogController(connectionName);
    }

    private static void printHelpToOutput() {
        System.out.println("Help for OpenPythia");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("without arguments   open in GUI mode");
        System.out.println("-t/-T               print this information");
        System.out.println("-c                  create a dummy configuration file (to have a template)");
        System.out.println("-s/-S               take snapshot and exit");
        System.out.println("                    To take a snapshot the JDBC driver must be available in the");
        System.out.println("                    class path or via the configuration.");
        System.out.println("                    -host        host to connect");
        System.out.println("                    -port        port to connect");
        System.out.println("                    -sid         SID to connect");
        System.out.println("                    -service     service name to connect");
        System.out.println("                    -tns         TNS name to connect");
        System.out.println("                     The parameters SID, service and TNS are processed in the\n" +
                           "                     order SID, service, TNS: If a SID and a service name are\n" +
                           "                     give the service name is ignored.");
        System.out.println("                    -user        user to connect");
        System.out.println("                    -pw          password of the user ");
        System.out.println("                    -f           optional - define a prefix for the snapshot file");
        System.out.println("                    -p           optional - define the path for the snapshot file");
        System.out.println("                    -loadsqltext optional - load the sql text into the snapshot");
        System.out.println("                    -jdbcdriver  optional - load the JDBC driver from the given\n" +
                           "                                 location");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Examples");
        System.out.println("Start in GUI mode:");
        System.out.println("  OpenPythia");
        System.out.println();
        System.out.println("Take a snapshot from <hoat>, <port>, <user>, <password> and write it to\n" +
                "C:\\temp\\Test<Snapshot ID>:");
        System.out.println("OpenPythia -s -host <host> -port <port> -user <user> -pw <password> -p C:/temp/\n" +
                "    -f Test");
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void startBatchModeTakeSnapshot(List<String> arguments) {
        String host = extractParameter(arguments, "-host");
        String port = extractParameter(arguments, "-port");
        String sid = extractParameter(arguments, "-sid");
        String serviceName = extractParameter(arguments, "-service");
        String tnsName = extractParameter(arguments, "-tns");
        String user = extractParameter(arguments, "-user");
        String password = extractParameter(arguments, "-pw");
        String filePrefix = extractParameter(arguments, "-f");
        String filePath = extractParameter(arguments, "-p");
        boolean loadSQLText = arguments.contains("-loadsqltext");
        String jdbcPath = extractParameter(arguments, "-jdbcdriver");

        DBConnectionInformation dbConnectionInformation = new DBConnectionInformation(
                host,
                port,
                sid,
                serviceName,
                tnsName,
                user,
                password,
                filePrefix,
                filePath,
                jdbcPath);

        BatchTakeSnapshot batch = new BatchTakeSnapshot(dbConnectionInformation);
        batch.takeSnapshot(loadSQLText);
        batch.shutDown();
    }

    private static String extractParameter(List<String> arguments, String parameter){
        if (arguments.indexOf(parameter) >= 0) {
            return arguments.get(arguments.indexOf(parameter) + 1);
        } else {
            return null;
        }
    }
    public static void gracefulExit() {
        ConnectionPoolUtils.shutdownPool();
        System.exit(0);
    }
}
