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
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
public class AboutView extends JDialog {
    public AboutView(Frame owner) {
        super(owner);
        initComponents();
    }

    public AboutView(Dialog owner) {
        super(owner);
        initComponents();
    }

    public JEditorPane getEditorPaneAbout() {
        return editorPaneAbout;
    }

    public JButton getBtnOK() {
        return btnOK;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        editorPaneAbout = new JEditorPane();
        buttonBar = new JPanel();
        btnOK = new JButton();

        //======== this ========
        setTitle("About OpenPythia");
        setModal(true);
        setMinimumSize(new Dimension(400, 200));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
                    "100dlu, $lgap, default:grow"));

                //======== scrollPane1 ========
                {

                    //---- editorPaneAbout ----
                    editorPaneAbout.setEditable(false);
                    scrollPane1.setViewportView(editorPaneAbout);
                }
                contentPanel.add(scrollPane1, CC.xywh(1, 1, 3, 3));
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
    private JScrollPane scrollPane1;
    private JEditorPane editorPaneAbout;
    private JPanel buttonBar;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
