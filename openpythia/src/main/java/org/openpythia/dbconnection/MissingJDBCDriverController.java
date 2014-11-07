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
package org.openpythia.dbconnection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.io.IOUtils;
import org.openpythia.utilities.FileSelectorUtility;

public class MissingJDBCDriverController {

    private static final String MISSING_JDBCDRIVER_HTML = "missingJDBCdriver.html";
    private MissingJDBCDriverView view;

    private String pathJDBCDriver = null;

    public MissingJDBCDriverController() {

        view = new MissingJDBCDriverView();

        prepareTextArea();

        bindActions();

        view.setSize(600, 400);
        view.setVisible(true);
    }

    public String getPathJDBCDriver() {
        return pathJDBCDriver;
    }

    private void prepareTextArea() {
        view.getEditorPaneMissingJDBCDriver().setContentType("text/html");

        String missingJDBCdriverText = "";

        try {
            InputStream inputStream =  this.getClass().getResourceAsStream(MISSING_JDBCDRIVER_HTML);
            missingJDBCdriverText = IOUtils.toString(inputStream);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        view.getEditorPaneMissingJDBCDriver().setText(missingJDBCdriverText);

        view.getEditorPaneMissingJDBCDriver().addHyperlinkListener(
                new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent evt) {
                        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            try {
                                Desktop.getDesktop().browse(
                                        evt.getURL().toURI());
                            } catch (Exception e) {
                                // If we can't open the URL - for what reason
                                // ever - we ignore it
                            }
                        }
                    }
                });
    }

    private void bindActions() {
        view.getBtnLoadDriver().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleButtonLoadDriver();
            }
        });
        view.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleButtonCancel();
            }
        });
    }

    private void handleButtonCancel() {
        view.dispose();
    }

    private void handleButtonLoadDriver() {
        File driverJARFile = FileSelectorUtility.chooseJarFileToRead();
        if (driverJARFile != null) {
            pathJDBCDriver = driverJARFile.getAbsolutePath();
            view.dispose();
        }
    }
}
