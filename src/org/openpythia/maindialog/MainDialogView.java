/*
 * Created by JFormDesigner on Fri Jun 03 08:36:12 CEST 2011
 */

package org.openpythia.maindialog;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Andreas Rothmann
 */
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
        // Generated using JFormDesigner non-commercial license
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        miQuit = new JMenuItem();
        menu2 = new JMenu();
        miOnlineHelp = new JMenuItem();
        miAbout = new JMenuItem();
        scrollPane1 = new JScrollPane();
        panelOverview = new JPanel();
        scrollPane2 = new JScrollPane();
        panelDetails = new JPanel();

        //======== this ========
        setTitle("Pythia");
        setMinimumSize(new Dimension(640, 480));
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "2*($lcgap, 200dlu), $lcgap, default:grow, $lcgap",
            "$lgap, 160dlu, $lgap, default:grow, $lgap"));

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
                panelOverview.setLayout(new BoxLayout(panelOverview, BoxLayout.Y_AXIS));
            }
            scrollPane1.setViewportView(panelOverview);
        }
        contentPane.add(scrollPane1, CC.xywh(2, 2, 1, 3));

        //======== scrollPane2 ========
        {

            //======== panelDetails ========
            {
                panelDetails.setLayout(new BoxLayout(panelDetails, BoxLayout.Y_AXIS));
            }
            scrollPane2.setViewportView(panelDetails);
        }
        contentPane.add(scrollPane2, CC.xywh(4, 2, 3, 3));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem miQuit;
    private JMenu menu2;
    private JMenuItem miOnlineHelp;
    private JMenuItem miAbout;
    private JScrollPane scrollPane1;
    private JPanel panelOverview;
    private JScrollPane scrollPane2;
    private JPanel panelDetails;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
