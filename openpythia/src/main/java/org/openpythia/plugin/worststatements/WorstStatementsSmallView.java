/*
 * Created by JFormDesigner on Mon Jan 14 10:39:17 CET 2013
 */

package org.openpythia.plugin.worststatements;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author r b
 */
public class WorstStatementsSmallView extends JPanel {
    public WorstStatementsSmallView() {
        initComponents();
    }

    public JTextField getTfTotalNumber() {
        return tfTotalNumber;
    }

    public JTextField getTfElapsedTop20() {
        return tfElapsedTop20;
    }

    public JButton getBtnShowDetails() {
        return btnShowDetails;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        tfTotalNumber = new JTextField();
        label2 = new JLabel();
        tfElapsedTop20 = new JTextField();
        label3 = new JLabel();
        btnShowDetails = new JButton();

        //======== this ========
        setBorder(new TitledBorder("Worst SQL Statements"));
        setLayout(new FormLayout(
            "left:110dlu, $lcgap, 35dlu, 0px, default",
            "2*(default, $lgap), top:default"));

        //---- label1 ----
        label1.setText("Total Number of SQL Statements");
        add(label1, CC.xy(1, 1));

        //---- tfTotalNumber ----
        tfTotalNumber.setEditable(false);
        tfTotalNumber.setHorizontalAlignment(SwingConstants.RIGHT);
        add(tfTotalNumber, CC.xy(3, 1));

        //---- label2 ----
        label2.setText("Elapsed Time for Top 20");
        add(label2, CC.xy(1, 3));

        //---- tfElapsedTop20 ----
        tfElapsedTop20.setEditable(false);
        tfElapsedTop20.setHorizontalAlignment(SwingConstants.RIGHT);
        add(tfElapsedTop20, CC.xy(3, 3));

        //---- label3 ----
        label3.setText("%");
        add(label3, CC.xy(5, 3));

        //---- btnShowDetails ----
        btnShowDetails.setText("Details");
        btnShowDetails.setMargin(new Insets(2, 5, 2, 5));
        add(btnShowDetails, CC.xy(1, 5, CC.LEFT, CC.DEFAULT));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JTextField tfTotalNumber;
    private JLabel label2;
    private JTextField tfElapsedTop20;
    private JLabel label3;
    private JButton btnShowDetails;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
