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
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog((Component) null, e);
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
