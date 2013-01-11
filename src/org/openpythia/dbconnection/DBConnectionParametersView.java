/*
 * Created by JFormDesigner on Wed Jun 01 16:10:02 CEST 2011
 */

package org.openpythia.dbconnection;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Andreas Rothmann
 */
public class DBConnectionParametersView extends JDialog {
    public DBConnectionParametersView() {
        initComponents();
    }

    public JTextField getTfHost() {
        return tfHost;
    }

    public JTextField getTfPort() {
        return tfPort;
    }

    public JTextField getTfDatabaseName() {
        return tfDatabaseName;
    }

    public JTextField getTfSchema() {
        return TfSchema;
    }

    public JPasswordField getTfPassword() {
        return tfPassword;
    }

    public JButton getBtnOK() {
        return btnOK;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public JButton getBtnSchemaCreation() {
        return btnSchemaCreation;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        label1 = new JLabel();
        tfHost = new JTextField();
        btnSchemaCreation = new JButton();
        label2 = new JLabel();
        tfPort = new JTextField();
        label3 = new JLabel();
        tfDatabaseName = new JTextField();
        label4 = new JLabel();
        TfSchema = new JTextField();
        label5 = new JLabel();
        tfPassword = new JPasswordField();
        btnOK = new JButton();
        btnCancel = new JButton();

        //======== this ========
        setModal(true);
        setTitle("Connection Parameters");
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default, 2*($lcgap, 80dlu)",
            "5*(default, $lgap), default"));

        //---- label1 ----
        label1.setText("Host");
        contentPane.add(label1, CC.xy(1, 1));
        contentPane.add(tfHost, CC.xy(3, 1));

        //---- btnSchemaCreation ----
        btnSchemaCreation.setText("<html>Schema creation<br>script</html>");
        contentPane.add(btnSchemaCreation, CC.xywh(5, 1, 1, 3));

        //---- label2 ----
        label2.setText("Port");
        contentPane.add(label2, CC.xy(1, 3));
        contentPane.add(tfPort, CC.xy(3, 3));

        //---- label3 ----
        label3.setText("Database Name");
        contentPane.add(label3, CC.xy(1, 5));
        contentPane.add(tfDatabaseName, CC.xy(3, 5));

        //---- label4 ----
        label4.setText("User / Schema");
        contentPane.add(label4, CC.xy(1, 7));
        contentPane.add(TfSchema, CC.xy(3, 7));

        //---- label5 ----
        label5.setText("Password");
        contentPane.add(label5, CC.xy(1, 9));
        contentPane.add(tfPassword, CC.xy(3, 9));

        //---- btnOK ----
        btnOK.setText("OK");
        contentPane.add(btnOK, CC.xy(3, 11));

        //---- btnCancel ----
        btnCancel.setText("Cancel");
        contentPane.add(btnCancel, CC.xy(5, 11));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JTextField tfHost;
    private JButton btnSchemaCreation;
    private JLabel label2;
    private JTextField tfPort;
    private JLabel label3;
    private JTextField tfDatabaseName;
    private JLabel label4;
    private JTextField TfSchema;
    private JLabel label5;
    private JPasswordField tfPassword;
    private JButton btnOK;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
