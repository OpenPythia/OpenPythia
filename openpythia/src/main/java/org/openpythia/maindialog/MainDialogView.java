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
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
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

    public JPanel getPanelDetails() {
        return panelDetails;
    }

    public JProgressBar getProgressBarMemory() {
        return progressBarMemory;
    }

    public JLabel getLblTotalMemory() {
        return lblTotalMemory;
    }

    public JPanel getPanelOverview() {
        return panelOverview;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar = new JMenuBar();
        menu1 = new JMenu();
        miQuit = new JMenuItem();
        menu2 = new JMenu();
        miOnlineHelp = new JMenuItem();
        miAbout = new JMenuItem();
        panelGlobalLayout = new JPanel();
        scrollPaneOverview = new JScrollPane();
        panelOverview = new JPanel();
        scrollPaneDetail = new JScrollPane();
        panelDetails = new JPanel();
        panelMemory = new JPanel();
        lblMemoryUsed = new JLabel();
        progressBarMemory = new JProgressBar();
        lblTotalMemory = new JLabel();

        //======== this ========
        setTitle("Pythia");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar ========
        {

            //======== menu1 ========
            {
                menu1.setText("File");

                //---- miQuit ----
                miQuit.setText("Quit");
                menu1.add(miQuit);
            }
            menuBar.add(menu1);

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
            menuBar.add(menu2);
        }
        setJMenuBar(menuBar);

        //======== panelGlobalLayout ========
        {
            panelGlobalLayout.setLayout(new FormLayout(
                "$lcgap, default, $lcgap, default:grow, $lcgap",
                "$lgap, fill:100dlu, $lgap, fill:default:grow, $lgap, default, $lgap"));

            //======== scrollPaneOverview ========
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
                scrollPaneOverview.setViewportView(panelOverview);
            }
            panelGlobalLayout.add(scrollPaneOverview, CC.xywh(2, 2, 1, 3));

            //======== scrollPaneDetail ========
            {

                //======== panelDetails ========
                {
                    panelDetails.setBorder(BorderFactory.createEmptyBorder());
                    panelDetails.setLayout(new BorderLayout());
                }
                scrollPaneDetail.setViewportView(panelDetails);
            }
            panelGlobalLayout.add(scrollPaneDetail, CC.xywh(4, 2, 1, 3));

            //======== panelMemory ========
            {
                panelMemory.setLayout(new FormLayout(
                    "default:grow, $lcgap, default, $lcgap, 30dlu, $lcgap, default",
                    "default"));

                //---- lblMemoryUsed ----
                lblMemoryUsed.setText("Memory used");
                panelMemory.add(lblMemoryUsed, CC.xy(3, 1));
                panelMemory.add(progressBarMemory, CC.xy(5, 1));

                //---- lblTotalMemory ----
                lblTotalMemory.setText("MB");
                panelMemory.add(lblTotalMemory, CC.xy(7, 1));
            }
            panelGlobalLayout.add(panelMemory, CC.xywh(2, 6, 3, 1));
        }
        contentPane.add(panelGlobalLayout, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar;
    private JMenu menu1;
    private JMenuItem miQuit;
    private JMenu menu2;
    private JMenuItem miOnlineHelp;
    private JMenuItem miAbout;
    private JPanel panelGlobalLayout;
    private JScrollPane scrollPaneOverview;
    private JPanel panelOverview;
    private JScrollPane scrollPaneDetail;
    private JPanel panelDetails;
    private JPanel panelMemory;
    private JLabel lblMemoryUsed;
    private JProgressBar progressBarMemory;
    private JLabel lblTotalMemory;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
