package org.openpythia.dbconnection;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.openpythia.utilities.FileRessourceUtility;
import org.openpythia.utilities.FileSelectorUtility;

public class MissingJDBCDriverController {

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
        String missingJDBCdriverText = FileRessourceUtility
                .getStringFromRessource("missingJDBCdriver.html");
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
