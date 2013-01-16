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
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
public class MissingJDBCDriverView extends JDialog {

    public MissingJDBCDriverView() {
        initComponents();
    }

    public JEditorPane getEditorPaneMissingJDBCDriver() {
        return editorPaneMissingJDBCDriver;
    }

    public JButton getBtnLoadDriver() {
        return btnLoadDriver;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        editorPaneMissingJDBCDriver = new JEditorPane();
        btnLoadDriver = new JButton();
        btnCancel = new JButton();

        //======== this ========
        setTitle("Missing JDBC Driver");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "80dlu, $lcgap, default:grow, $lcgap, 80dlu",
            "40dlu, $lgap, default:grow, $lgap, default"));

        //======== scrollPane1 ========
        {

            //---- editorPaneMissingJDBCDriver ----
            editorPaneMissingJDBCDriver.setMinimumSize(new Dimension(600, 400));
            editorPaneMissingJDBCDriver.setEditable(false);
            scrollPane1.setViewportView(editorPaneMissingJDBCDriver);
        }
        contentPane.add(scrollPane1, CC.xywh(1, 1, 5, 3));

        //---- btnLoadDriver ----
        btnLoadDriver.setText("Load JDBC driver");
        contentPane.add(btnLoadDriver, CC.xy(1, 5));

        //---- btnCancel ----
        btnCancel.setText("Cancel");
        contentPane.add(btnCancel, CC.xy(5, 5));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JEditorPane editorPaneMissingJDBCDriver;
    private JButton btnLoadDriver;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
