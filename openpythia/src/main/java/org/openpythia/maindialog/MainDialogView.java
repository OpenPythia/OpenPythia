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
package org.openpythia.maindialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
public class MainDialogView extends JFrame {
    public MainDialogView() {
        initComponents();
    }

    public JMenuItem getMiQuit() {
        return miQuit;
    }

    public JMenuItem getMiOnlineHelp() {
        return miOnlineHelp;
    }

    public JMenuItem getMiAbout() {
        return miAbout;
    }

    public JPanel getPanelOverview() {
        return panelOverview;
    }

    public JPanel getPanelDetails() {
        return panelDetails;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        miQuit = new JMenuItem();
        menu2 = new JMenu();
        miOnlineHelp = new JMenuItem();
        miAbout = new JMenuItem();
        scrollPane1 = new JScrollPane();
        panelOverview = new JPanel();
        panelDetails = new JPanel();

        //======== this ========
        setTitle("Pythia");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("File");

                //---- miQuit ----
                miQuit.setText("Quit");
                menu1.add(miQuit);
            }
            menuBar1.add(menu1);

            //======== menu2 ========
            {
                menu2.setText("Help");

                //---- miOnlineHelp ----
                miOnlineHelp.setText("Open Online Help");
                menu2.add(miOnlineHelp);
                menu2.addSeparator();

                //---- miAbout ----
                miAbout.setText("About");
                menu2.add(miAbout);
            }
            menuBar1.add(menu2);
        }
        setJMenuBar(menuBar1);

        //======== scrollPane1 ========
        {

            //======== panelOverview ========
            {
                panelOverview.setBorder(new EmptyBorder(5, 5, 5, 5));
                panelOverview.setLayout(new GridBagLayout());
                ((GridBagLayout)panelOverview.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)panelOverview.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)panelOverview.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)panelOverview.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
            }
            scrollPane1.setViewportView(panelOverview);
        }
        contentPane.add(scrollPane1, BorderLayout.WEST);

        //======== panelDetails ========
        {
            panelDetails.setBorder(new EmptyBorder(5, 5, 5, 5));
            panelDetails.setLayout(new BorderLayout());
        }
        contentPane.add(panelDetails, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem miQuit;
    private JMenu menu2;
    private JMenuItem miOnlineHelp;
    private JMenuItem miAbout;
    private JScrollPane scrollPane1;
    private JPanel panelOverview;
    private JPanel panelDetails;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
