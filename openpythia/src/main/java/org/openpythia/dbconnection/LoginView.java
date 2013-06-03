/*
 * Created by JFormDesigner on Tue Jan 15 13:51:21 CET 2013
 */

package org.openpythia.dbconnection;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.MaskFormatter;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Dr. Jürgen Tenckhoff
 */
public class LoginView extends JDialog {
    public LoginView(Frame owner) {
        super(owner);
        initComponents();
    }

    public LoginView(Dialog owner) {
        super(owner);
        initComponents();
    }

    public JButton getButtonConnect() {
        return buttonConnect;
    }

    public JButton getButtonCancel() {
        return buttonCancel;
    }

    public JTextField getTextFieldConnectionName() {
        return textFieldConnectionName;
    }

    public JTextField getTextFieldHost() {
        return textFieldHost;
    }

    public JFormattedTextField getTextFieldPort() {
        return textFieldPort;
    }

    public JTextField getTextFieldDatabaseName() {
        return textFieldDatabaseName;
    }

    public JTextField getTextFieldUser() {
        return textFieldUser;
    }

    public JPasswordField getTextFieldPassword() {
        return textFieldPassword;
    }

    public JList getSavedConnectionsList() {
        return savedConnectionsList;
    }

    public JButton getButtonRemoveSavedConnection() {
        return buttonRemoveSavedConnection;
    }

    public JButton getButtonAdd() {
        return buttonAdd;
    }

    public JMenuItem getMenuCreateSchemaScript() {
        return menuCreateSchemaScript;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuCreateSchemaScript = new JMenuItem();
        label10 = new JLabel();
        buttonAdd = new JButton();
        buttonRemoveSavedConnection = new JButton();
        scrollPane1 = new JScrollPane();
        savedConnectionsList = new JList();
        label11 = new JLabel();
        textFieldConnectionName = new JTextField();
        label5 = new JLabel();
        textFieldHost = new JTextField();
        label6 = new JLabel();
        textFieldPort = new JFormattedTextField();
        label7 = new JLabel();
        textFieldDatabaseName = new JTextField();
        label8 = new JLabel();
        textFieldUser = new JTextField();
        label9 = new JLabel();
        textFieldPassword = new JPasswordField();
        panel4 = new JPanel();
        buttonConnect = new JButton();
        buttonCancel = new JButton();

        //======== this ========
        setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$lcgap, default, $ugap, default, $lcgap, default, $ugap, pref, $lcgap, 80dlu:grow, $lcgap",
            "$lcgap, pref, default, $lgap, pref, 4*($lgap, default), $pgap, default, $lgap"));

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("Tools");

                //---- menuCreateSchemaScript ----
                menuCreateSchemaScript.setText("Create schema creation script...");
                menu1.add(menuCreateSchemaScript);
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

        //---- label10 ----
        label10.setText("Saved connection configurations:");
        contentPane.add(label10, CC.xy(2, 2));

        //---- buttonAdd ----
        buttonAdd.setBorder(new EtchedBorder());
        buttonAdd.setIcon(new ImageIcon(getClass().getResource("/plus.gif")));
        buttonAdd.setToolTipText("Save or update current connection configuration");
        contentPane.add(buttonAdd, CC.xy(4, 2));

        //---- buttonRemoveSavedConnection ----
        buttonRemoveSavedConnection.setBorder(new EtchedBorder());
        buttonRemoveSavedConnection.setIcon(new ImageIcon(getClass().getResource("/minus.gif")));
        buttonRemoveSavedConnection.setToolTipText("Remove selected connection configuration");
        contentPane.add(buttonRemoveSavedConnection, CC.xy(6, 2));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(savedConnectionsList);
        }
        contentPane.add(scrollPane1, CC.xywh(2, 3, 5, 13));

        //---- label11 ----
        label11.setText("Connection name");
        contentPane.add(label11, CC.xy(8, 3));
        contentPane.add(textFieldConnectionName, CC.xy(10, 3));

        //---- label5 ----
        label5.setText("Host");
        contentPane.add(label5, CC.xy(8, 5));
        contentPane.add(textFieldHost, CC.xy(10, 5));

        //---- label6 ----
        label6.setText("Port");
        contentPane.add(label6, CC.xy(8, 7));

        //---- textFieldPort ----
        textFieldPort.setFormatterFactory(null);
        contentPane.add(textFieldPort, CC.xy(10, 7));

        //---- label7 ----
        label7.setText("Database name");
        contentPane.add(label7, CC.xy(8, 9));
        contentPane.add(textFieldDatabaseName, CC.xy(10, 9));

        //---- label8 ----
        label8.setText("User / Schema");
        contentPane.add(label8, CC.xy(8, 11));
        contentPane.add(textFieldUser, CC.xy(10, 11));

        //---- label9 ----
        label9.setText("Password");
        contentPane.add(label9, CC.xy(8, 13));
        contentPane.add(textFieldPassword, CC.xy(10, 13));

        //======== panel4 ========
        {
            panel4.setLayout(new FlowLayout());

            //---- buttonConnect ----
            buttonConnect.setText("Connect");
            panel4.add(buttonConnect);

            //---- buttonCancel ----
            buttonCancel.setText("Cancel");
            panel4.add(buttonCancel);
        }
        contentPane.add(panel4, CC.xywh(8, 15, 3, 1));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuCreateSchemaScript;
    private JLabel label10;
    private JButton buttonAdd;
    private JButton buttonRemoveSavedConnection;
    private JScrollPane scrollPane1;
    private JList savedConnectionsList;
    private JLabel label11;
    private JTextField textFieldConnectionName;
    private JLabel label5;
    private JTextField textFieldHost;
    private JLabel label6;
    private JFormattedTextField textFieldPort;
    private JLabel label7;
    private JTextField textFieldDatabaseName;
    private JLabel label8;
    private JTextField textFieldUser;
    private JLabel label9;
    private JPasswordField textFieldPassword;
    private JPanel panel4;
    private JButton buttonConnect;
    private JButton buttonCancel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
