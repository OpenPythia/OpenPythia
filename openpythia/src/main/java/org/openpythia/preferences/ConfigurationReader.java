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

import java.beans.XMLDecoder;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: rabe
 * Date: 18.01.13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationReader {

    private InputStream inputStream;
    private XMLDecoder decoder;

    public ConfigurationReader(File inputFile) throws IOException {
        inputStream = new FileInputStream(inputFile);
    }

    public ConfigurationReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public PythiaConfiguration read() throws IOException {
        decoder = new XMLDecoder(inputStream);
        return (PythiaConfiguration)decoder.readObject();
    }

    public void close() {
        if(decoder != null) {
            decoder.close();
        }
    }
}
