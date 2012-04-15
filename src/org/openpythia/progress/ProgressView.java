/*
 * Created by JFormDesigner on Mon Jun 06 15:48:50 CEST 2011
 */

package org.openpythia.progress;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Andreas Rothmann
 */
public class ProgressView extends JDialog {
	
	// The dialogs are not designed to be serialized. But to avoid the warnings...
	private static final long serialVersionUID = 1L;

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
        // Generated using JFormDesigner non-commercial license
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
    // Generated using JFormDesigner non-commercial license
    private JLabel lblMessage;
    private JLabel lblStart;
    private JProgressBar progressBar;
    private JLabel lblEnd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
