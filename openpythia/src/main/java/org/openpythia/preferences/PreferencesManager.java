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
package org.openpythia.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class PreferencesManager {

    private static final String PREFERENCES_FILE_NAME = "Pythia.xml";
    private static PythiaConfiguration pythiaConfiguration;

    static {
        pythiaConfiguration = loadConfiguration();

        if(pythiaConfiguration == null) {
            pythiaConfiguration = new PythiaConfiguration();
        }
    }

    private PreferencesManager() {
    }

    public static String getPathToJDBCDriver() {
        return pythiaConfiguration.getPathToJDBCDriver();
    }

    public static void setPathToJDBCDriver(String pathToJDBCDriver) {
        pythiaConfiguration.setPathToJDBCDriver(pathToJDBCDriver);
    }

    public static void setLastConfiguration(ConnectionConfiguration lastConfiguration) {
        pythiaConfiguration.setLastConfiguration(lastConfiguration);
    }

    public static List<ConnectionConfiguration> getSavedConnectionConfiguration() {
        return pythiaConfiguration.getSavedConnectionConfigurations();
    }

    public static void addOrUpdateSavedConnectionConfiguration(ConnectionConfiguration connectionConfiguration) {
        List<ConnectionConfiguration> savedConnectionConfigurations = pythiaConfiguration.getSavedConnectionConfigurations();

        if(savedConnectionConfigurations.contains(connectionConfiguration)) {
            savedConnectionConfigurations.remove(connectionConfiguration);
        }

        pythiaConfiguration.getSavedConnectionConfigurations().add(connectionConfiguration);
    }

    public static void removeSavedConnectionConfiguration(ConnectionConfiguration connectionConfiguration) {
        pythiaConfiguration.getSavedConnectionConfigurations().remove(connectionConfiguration);
    }

    public static ConnectionConfiguration getLastConfiguration() {
        return pythiaConfiguration.getLastConfiguration();
    }

    public static void savePythiaConfiguration() {
        try {
            ConfigurationWriter writer = new ConfigurationWriter(getConfigurationFile());
            writer.write(pythiaConfiguration);
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PythiaConfiguration loadConfiguration() {
        try {
            ConfigurationReader reader = new ConfigurationReader(getConfigurationFile());
            return reader.read();
        }
        catch (FileNotFoundException e) {
            // ignore if file is
            return null;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File getConfigurationFile() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, PREFERENCES_FILE_NAME);
    }

}
