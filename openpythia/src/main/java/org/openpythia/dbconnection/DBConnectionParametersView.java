/*
 * Created by JFormDesigner on Mon Jan 14 13:18:03 CET 2013
 */

package org.openpythia.dbconnection;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Dr. Jürgen Tenckhoff
 */
public class DBConnectionParametersView extends JDialog {

    public DBConnectionParametersView() {
        initComponents();
    }

    public JTextField getTfHost() {
        return tfHost;
    }

    public JButton getBtnSchemaCreation() {
        return btnSchemaCreation;
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel2 = new JPanel();
        label1 = new JLabel();
        tfHost = new JTextField();
        separator1 = new JSeparator();
        btnSchemaCreation = new JButton();
        label2 = new JLabel();
        tfPort = new JTextField();
        label3 = new JLabel();
        tfDatabaseName = new JTextField();
        label4 = new JLabel();
        TfSchema = new JTextField();
        label5 = new JLabel();
        tfPassword = new JPasswordField();
        panel1 = new JPanel();
        btnOK = new JButton();
        btnCancel = new JButton();

        //======== this ========
        setModal(true);
        setTitle("Connection Parameters");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel2 ========
        {
            panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
            panel2.setLayout(new FormLayout(
                "default, $lcgap, 120dlu:grow, $lcgap, pref, $lcgap, default",
                "5*(default, $lgap), default"));

            //---- label1 ----
            label1.setText("Host");
            panel2.add(label1, CC.xy(1, 1));
            panel2.add(tfHost, CC.xy(3, 1));

            //---- separator1 ----
            separator1.setOrientation(SwingConstants.VERTICAL);
            panel2.add(separator1, CC.xywh(5, 1, 1, 11));

            //---- btnSchemaCreation ----
            btnSchemaCreation.setText("<html>Schema creation<br>script</html>");
            panel2.add(btnSchemaCreation, CC.xywh(7, 1, 1, 3));

            //---- label2 ----
            label2.setText("Port");
            panel2.add(label2, CC.xy(1, 3));
            panel2.add(tfPort, CC.xy(3, 3));

            //---- label3 ----
            label3.setText("Database Name");
            panel2.add(label3, CC.xy(1, 5));
            panel2.add(tfDatabaseName, CC.xy(3, 5));

            //---- label4 ----
            label4.setText("User / Schema");
            panel2.add(label4, CC.xy(1, 7));
            panel2.add(TfSchema, CC.xy(3, 7));

            //---- label5 ----
            label5.setText("Password");
            panel2.add(label5, CC.xy(1, 9));
            panel2.add(tfPassword, CC.xy(3, 9));

            //======== panel1 ========
            {
                panel1.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                panel1.setLayout(new FormLayout(
                    "$button, $lcgap, $button",
                    "fill:default"));

                //---- btnOK ----
                btnOK.setText("OK");
                panel1.add(btnOK, CC.xy(1, 1));

                //---- btnCancel ----
                btnCancel.setText("Cancel");
                panel1.add(btnCancel, CC.xy(3, 1));
            }
            panel2.add(panel1, CC.xywh(1, 11, 3, 1));
        }
        contentPane.add(panel2, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel2;
    private JLabel label1;
    private JTextField tfHost;
    private JSeparator separator1;
    private JButton btnSchemaCreation;
    private JLabel label2;
    private JTextField tfPort;
    private JLabel label3;
    private JTextField tfDatabaseName;
    private JLabel label4;
    private JTextField TfSchema;
    private JLabel label5;
    private JPasswordField tfPassword;
    private JPanel panel1;
    private JButton btnOK;
    private JButton btnCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
