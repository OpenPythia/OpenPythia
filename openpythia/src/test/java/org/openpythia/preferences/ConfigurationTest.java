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

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rabe
 * Date: 18.01.13
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationTest {

    @Test
    public void writeConfigurationTest() throws IOException {
        File file = File.createTempFile("test1", "xml");
        file.deleteOnExit();

        PythiaConfiguration originalConfiguration = buildTestConfiguration();

        ConfigurationWriter writer = new ConfigurationWriter(file);
        writer.write(originalConfiguration);
        writer.close();

        ConfigurationReader reader = new ConfigurationReader(file);
        PythiaConfiguration readConfiguration = reader.read();

        checkTestConfiguration(readConfiguration);
    }

    private PythiaConfiguration buildTestConfiguration() {
        PythiaConfiguration pythiaConfiguration = new PythiaConfiguration();
        pythiaConfiguration.setPathToJDBCDriver("/test/jdbc.jar");

        ConnectionConfiguration lastConnectionConfiguration = new ConnectionConfiguration(
                "lastconnection", "localhost", 1521,
                ConnectionTypeEnum.SID, "xe", "", "",
                "pythia", "confidential");

        pythiaConfiguration.setLastConfiguration(lastConnectionConfiguration);

        ConnectionConfiguration saveConnectionConfiguration = new ConnectionConfiguration(
                "production", "192.168.200.1", 1521,
                ConnectionTypeEnum.SID, "orcl", "", "",
                "pythia", "confidential");

        List<ConnectionConfiguration> savedConnectionConfigurations = new LinkedList<ConnectionConfiguration>();
        savedConnectionConfigurations.add(saveConnectionConfiguration);

        pythiaConfiguration.setSavedConnectionConfigurations(savedConnectionConfigurations);

        return pythiaConfiguration;
    }

    private void checkTestConfiguration(PythiaConfiguration pythiaConfiguration) {
        Assert.assertEquals("/test/jdbc.jar", pythiaConfiguration.getPathToJDBCDriver());

        Assert.assertEquals("lastconnection", pythiaConfiguration.getLastConfiguration().getConnectionName());
        Assert.assertEquals("localhost", pythiaConfiguration.getLastConfiguration().getHost());
        Assert.assertEquals(1521, (int) pythiaConfiguration.getLastConfiguration().getPort());
        Assert.assertEquals(ConnectionTypeEnum.SID, pythiaConfiguration.getLastConfiguration().getConnectionType());
        Assert.assertEquals("xe", pythiaConfiguration.getLastConfiguration().getSid());
        Assert.assertEquals("pythia", pythiaConfiguration.getLastConfiguration().getUser());
        Assert.assertNull(pythiaConfiguration.getLastConfiguration().getPassword());

        Assert.assertEquals("production", pythiaConfiguration.getSavedConnectionConfigurations().get(0).getConnectionName());
        Assert.assertEquals("192.168.200.1", pythiaConfiguration.getSavedConnectionConfigurations().get(0).getHost());
        Assert.assertEquals(1521, (int) pythiaConfiguration.getSavedConnectionConfigurations().get(0).getPort());
        Assert.assertEquals(ConnectionTypeEnum.SID, pythiaConfiguration.getSavedConnectionConfigurations().get(0).getConnectionType());
        Assert.assertEquals("orcl", pythiaConfiguration.getSavedConnectionConfigurations().get(0).getSid());
        Assert.assertEquals("pythia", pythiaConfiguration.getSavedConnectionConfigurations().get(0).getUser());
        Assert.assertNull(pythiaConfiguration.getSavedConnectionConfigurations().get(0).getPassword());

    }
}
