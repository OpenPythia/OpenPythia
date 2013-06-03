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

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents the overall configuration of openpythia
 */
public class PythiaConfiguration {

    private String pathToJDBCDriver;
    private ConnectionConfiguration lastConfiguration;
    private List<ConnectionConfiguration> savedConnectionConfigurations;

    public PythiaConfiguration() {
        savedConnectionConfigurations = new LinkedList<ConnectionConfiguration>();
    }

    public String getPathToJDBCDriver() {
        return pathToJDBCDriver;
    }

    public void setPathToJDBCDriver(String pathToJDBCDriver) {
        this.pathToJDBCDriver = pathToJDBCDriver;
    }

    public ConnectionConfiguration getLastConfiguration() {
        return lastConfiguration;
    }

    public void setLastConfiguration(ConnectionConfiguration lastConfiguration) {
        this.lastConfiguration = lastConfiguration;
    }

    public List<ConnectionConfiguration> getSavedConnectionConfigurations() {
        return savedConnectionConfigurations;
    }

    /** Package visible should only be used for configurations internal use **/
    public void setSavedConnectionConfigurations(List<ConnectionConfiguration> saveConfiguration) {
        this.savedConnectionConfigurations = saveConfiguration;
    }
}
