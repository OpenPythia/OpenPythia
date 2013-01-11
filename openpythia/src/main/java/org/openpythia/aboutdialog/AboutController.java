package org.openpythia.aboutdialog;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.openpythia.utilities.FileRessourceUtility;

public class AboutController {

    private AboutView view;

    public AboutController(JFrame owner) {

        view = new AboutView(owner);

        view.getEditorPaneAbout().setContentType("text/html");
        String aboutText = FileRessourceUtility
                .getStringFromRessource("about.html");
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
