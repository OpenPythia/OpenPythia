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
            PythiaMain.gracefulExit();
        }
    }

    private void buttonOKPressed() {
        view.dispose();

        PythiaMain.gracefulExit();
    }

}
