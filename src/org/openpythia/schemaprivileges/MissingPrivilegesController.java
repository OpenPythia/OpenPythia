package org.openpythia.schemaprivileges;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.openpythia.main.PythiaMain;

public class MissingPrivilegesController {

    private MissingPrivilegesView view;

    public MissingPrivilegesController(String sqlStatements) {
        view = new MissingPrivilegesView((Frame) null);

        view.getTextAreaSQLStatement().setText(sqlStatements);
        bindActions();
        view.addWindowListener(new CloseWindowListener());

        view.setVisible(true);
    }

    private void bindActions() {
        view.getBtnOK().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonOKPressed();
            }
        });
    }

    private static class CloseWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            PythiaMain.gracefullExit();
        }
    }

    private void buttonOKPressed() {
        view.dispose();

        PythiaMain.gracefullExit();
    }

}
