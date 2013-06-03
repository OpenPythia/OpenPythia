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

import java.beans.*;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: rabe
 * Date: 18.01.13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationWriter {

    private OutputStream outputStream;
    private XMLEncoder xmlEncoder;

    public ConfigurationWriter(File outputFile) throws IOException {
        this.outputStream = new FileOutputStream(outputFile);
    }

    public ConfigurationWriter(OutputStream outputStream) throws IOException {
        this.outputStream = outputStream;
    }

    public void write(PythiaConfiguration configuration) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(ConnectionConfiguration.class);

            for(PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                if(propertyDescriptor.getName().equals("password")) {
                    propertyDescriptor.setValue("transient", Boolean.TRUE);
                }
            }

            xmlEncoder = new XMLEncoder(outputStream);
            xmlEncoder.writeObject(configuration);
            xmlEncoder.flush();
        }
        catch (IntrospectionException e) {

        }
    }

    public void close() {
        if (xmlEncoder != null) {
            xmlEncoder.close();
        }
    }
}
