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
package org.openpythia.aboutdialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.io.IOUtils;
import org.openpythia.utilities.FileRessourceUtility;

public class AboutController {

    public static final String ABOUT_HTML = "about.html";
    private AboutView view;

    public AboutController(JFrame owner) {
        view = new AboutView(owner);
        view.getEditorPaneAbout().setContentType("text/html");

        String aboutText = "";

        try {
            InputStream inputStream = this.getClass().getResourceAsStream(ABOUT_HTML);
            aboutText = IOUtils.toString(inputStream);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        view.getEditorPaneAbout().setText(aboutText);

        view.getEditorPaneAbout().addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent evt) {
                if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(evt.getURL().toURI());
                    } catch (Exception e) {
                        // If we can't open the URL we just ignore it (no error
                        // message)
                    }
                }
            }
        });

        view.getBtnOK().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                view.dispose();
            }
        });

        view.getRootPane().setDefaultButton(view.getBtnOK());
        view.getBtnOK().requestFocus();

        view.setSize(600, 400);
        view.setVisible(true);
    }
}
