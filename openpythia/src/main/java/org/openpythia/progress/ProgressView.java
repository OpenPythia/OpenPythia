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
package org.openpythia.progress;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.*;
import javax.swing.*;
public class ProgressView extends JDialog {
    public ProgressView(Frame owner) {
        super(owner);
        initComponents();
    }

    public ProgressView(Dialog owner) {
        super(owner);
        initComponents();
    }

    public JLabel getLblMessage() {
        return lblMessage;
    }

    public JLabel getLblStart() {
        return lblStart;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JLabel getLblEnd() {
        return lblEnd;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblMessage = new JLabel();
        lblStart = new JLabel();
        progressBar = new JProgressBar();
        lblEnd = new JLabel();

        //======== this ========
        setMinimumSize(new Dimension(400, 200));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$lcgap, default, $lcgap, default:grow, $lcgap, default, $lcgap",
            "2*($lgap, default), $lgap"));

        //---- lblMessage ----
        lblMessage.setText("message for the user");
        contentPane.add(lblMessage, CC.xywh(2, 2, 5, 1));

        //---- lblStart ----
        lblStart.setText("Start");
        contentPane.add(lblStart, CC.xy(2, 4));
        contentPane.add(progressBar, CC.xy(4, 4));

        //---- lblEnd ----
        lblEnd.setText("End");
        contentPane.add(lblEnd, CC.xy(6, 4));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblMessage;
    private JLabel lblStart;
    private JProgressBar progressBar;
    private JLabel lblEnd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
