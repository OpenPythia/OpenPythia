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
package org.openpythia.utilities;

import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

public class PreferencesHandler {

    private static final String PREFERENCES_FILE_NAME = "Pythia.pref";

    private static final String JDBCDRIVER_FILE_NAME = "JDBCDriverFileName";
    private static final String CONNECTION_HOST = "ConnectionHost";
    private static final String CONNECTION_PORT = "ConnectionPort";
    private static final String CONNECTION_DATABASE_NAME = "ConnectionDatabaseName";
    private static final String CONNECTION_SCHEMA = "ConnectionSchema";

    static PreferencesHandler handler;

    private Properties preferencesProperties;

    private PreferencesHandler() {
        preferencesProperties = loadPropertiesFromFile();

    }

    private Properties loadPropertiesFromFile() {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(PREFERENCES_FILE_NAME);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            // if there is no such file it not an error: The first time Pythia
            // was starts there is never a file...
        } catch (IOException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }
        return properties;
    }

    private void savePreferencesToFile() {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(PREFERENCES_FILE_NAME);
            preferencesProperties.store(output, "Pythia Preferences");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private static synchronized PreferencesHandler getInstance() {
        if (handler == null) {
            handler = new PreferencesHandler();
        }
        return handler;
    }

    public static String getJDBCDriverFileName() {
        return getInstance().preferencesProperties
                .getProperty(JDBCDRIVER_FILE_NAME);
    }

    public static void setJDBCDriverFileName(String fileName) {
        if (fileName == null) {
            getInstance().preferencesProperties.remove(JDBCDRIVER_FILE_NAME);
        } else {
            getInstance().preferencesProperties.setProperty(
                    JDBCDRIVER_FILE_NAME, fileName);
        }
        getInstance().savePreferencesToFile();
    }

    public static String getHost() {
        return getInstance().preferencesProperties.getProperty(CONNECTION_HOST);
    }

    public static void setHost(String host) {
        if (host == null) {
            getInstance().preferencesProperties.remove(CONNECTION_HOST);
        } else {
            getInstance().preferencesProperties.setProperty(CONNECTION_HOST,
                    host);
        }
        getInstance().savePreferencesToFile();
    }

    public static String getPort() {
        return getInstance().preferencesProperties.getProperty(CONNECTION_PORT);
    }

    public static void setPort(String port) {
        if (port == null) {
            getInstance().preferencesProperties.remove(CONNECTION_PORT);
        } else {
            getInstance().preferencesProperties.setProperty(CONNECTION_PORT,
                    port);
        }
        getInstance().savePreferencesToFile();
    }

    public static String getDatabaseName() {
        return getInstance().preferencesProperties
                .getProperty(CONNECTION_DATABASE_NAME);
    }

    public static void setDatabaseName(String databaseName) {
        if (databaseName == null) {
            getInstance().preferencesProperties
                    .remove(CONNECTION_DATABASE_NAME);
        } else {
            getInstance().preferencesProperties.setProperty(
                    CONNECTION_DATABASE_NAME, databaseName);
        }
        getInstance().savePreferencesToFile();
    }

    public static String getSchemaName() {
        return getInstance().preferencesProperties
                .getProperty(CONNECTION_SCHEMA);
    }

    public static void setSchemaName(String schemaName) {
        if (schemaName == null) {
            getInstance().preferencesProperties.remove(CONNECTION_SCHEMA);
        } else {
            getInstance().preferencesProperties.setProperty(CONNECTION_SCHEMA,
                    schemaName);
        }
        getInstance().savePreferencesToFile();
    }
}
