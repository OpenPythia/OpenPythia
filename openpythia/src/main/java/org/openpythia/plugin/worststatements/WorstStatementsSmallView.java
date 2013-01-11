/*
 * Created by JFormDesigner on Mon Jun 06 10:41:28 CEST 2011
 */

package org.openpythia.plugin.worststatements;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Andreas Rothmann
 */
public class WorstStatementsSmallView extends JPanel {
	
	// The dialogs are not designed to be serialized. But to avoid the warnings...
	private static final long serialVersionUID = 1L;

    public WorstStatementsSmallView() {
        initComponents();
    }

    public JTextField getTfTotalNumber() {
        return tfTotalNumber;
    }

    public JButton getBtnShowDetails() {
        return btnShowDetails;
    }

    public JTextField getTfElapsedTop20() {
        return tfElapsedTop20;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        tfTotalNumber = new JTextField();
        btnShowDetails = new JButton();
        label2 = new JLabel();
        tfElapsedTop20 = new JTextField();

        //======== this ========
        setBorder(new TitledBorder("Worst SQL Statements"));
        setMaximumSize(new Dimension(400, 83));
        setLayout(new FormLayout(
            "110dlu, $lcgap, 35dlu, $lcgap, default",
            "default, $lgap, default"));

        //---- label1 ----
        label1.setText("Total Number of SQL Statements");
        add(label1, CC.xy(1, 1));

        //---- tfTotalNumber ----
        tfTotalNumber.setEditable(false);
        add(tfTotalNumber, CC.xy(3, 1));

        //---- btnShowDetails ----
        btnShowDetails.setText("Details");
        btnShowDetails.setMargin(new Insets(2, 5, 2, 5));
        add(btnShowDetails, CC.xywh(5, 1, 1, 3));

        //---- label2 ----
        label2.setText("Elapsed Time for Top 20");
        add(label2, CC.xy(1, 3));

        //---- tfElapsedTop20 ----
        tfElapsedTop20.setEditable(false);
        add(tfElapsedTop20, CC.xy(3, 3));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField tfTotalNumber;
    private JButton btnShowDetails;
    private JLabel label2;
    private JTextField tfElapsedTop20;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
