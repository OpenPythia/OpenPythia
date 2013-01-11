/*
 * Created by JFormDesigner on Fri Jun 03 12:10:40 CEST 2011
 */

package org.openpythia.aboutdialog;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Andreas Rothmann
 */
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
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        editorPaneAbout = new JEditorPane();
        buttonBar = new JPanel();
        btnOK = new JButton();

        //======== this ========
        setTitle("About Pythia");
        setModal(true);
        setMinimumSize(new Dimension(400, 200));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
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
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
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
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane scrollPane1;
    private JEditorPane editorPaneAbout;
    private JPanel buttonBar;
    private JButton btnOK;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
