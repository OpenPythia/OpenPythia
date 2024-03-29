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

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
public class MissingPrivilegesView extends JDialog {
    public MissingPrivilegesView(Frame owner) {
        super(owner);
        initComponents();
    }

    public MissingPrivilegesView(Dialog owner) {
        super(owner);
        initComponents();
    }

    public JTextArea getTextAreaSQLStatement() {
        return textAreaSQLStatement;
    }

    public JButton getBtnOK() {
        return btnOK;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        scrollPane1 = new JScrollPane();
        textAreaSQLStatement = new JTextArea();
        label3 = new JLabel();
        label4 = new JLabel();
        buttonBar = new JPanel();
        btnOK = new JButton();

        //======== this ========
        setTitle("Missing Privileges");
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(400, 250));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(BorderFactory.createEmptyBorder());
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                    "200dlu, $lcgap, default:grow",
                    "2*(default, $lgap), fill:50dlu, $lgap, fill:default:grow, 2*($lgap, default)"));

                //---- label1 ----
                label1.setText("The schema is lacking some privileges that are needed by Pythia.");
                contentPanel.add(label1, CC.xywh(1, 1, 3, 1));

                //---- label2 ----
                label2.setText("Find the SQL statements to grant these privileges next.");
                contentPanel.add(label2, CC.xywh(1, 3, 3, 1));

                //======== scrollPane1 ========
                {

                    //---- textAreaSQLStatement ----
                    textAreaSQLStatement.setEditable(false);
                    scrollPane1.setViewportView(textAreaSQLStatement);
                }
                contentPanel.add(scrollPane1, CC.xywh(1, 5, 3, 3));

                //---- label3 ----
                label3.setText("Please ask an administrator to execute these statements so your schema gets the needed privileges.");
                contentPanel.add(label3, CC.xywh(1, 9, 3, 1));

                //---- label4 ----
                label4.setText("Pythia will exit now. Please come back when your schema has all needed privileges.");
                contentPanel.add(label4, CC.xywh(1, 11, 3, 1));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(BorderFactory.createEmptyBorder());
                buttonBar.setLayout(new FormLayout(
                    "$glue, $button",
                    "pref"));

                //---- btnOK ----
                btnOK.setText("OK");
                buttonBar.add(btnOK, CC.xy(2, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JLabel label2;
    private JScrollPane scrollPane1;
    private JTextArea textAreaSQLStatement;
    private JLabel label3;
    private JLabel label4;
    private JPanel buttonBar;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
