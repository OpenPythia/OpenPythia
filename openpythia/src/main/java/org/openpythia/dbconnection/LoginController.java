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
package org.openpythia.dbconnection;

import org.openpythia.preferences.ConnectionConfiguration;
import org.openpythia.preferences.PreferencesManager;
import org.openpythia.schemaprivileges.PrivilegesHelper;
import org.openpythia.utilities.FileSelectorUtility;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LoginController {

    public static enum LoginResult {
        OK,CANCEL
    }

    private static class ConnectionListModel extends AbstractListModel {

        private List<ConnectionConfiguration> connections;

        public ConnectionListModel(List<ConnectionConfiguration> connections) {
            this.connections = new LinkedList<ConnectionConfiguration>(connections);
        }

        @Override
        public int getSize() {
            return connections.size();
        }

        @Override
        public ConnectionConfiguration getElementAt(int index) {
            return connections.get(index);
        }

        public void addOrUpdate(ConnectionConfiguration connectionConfiguration) {
            int index = connections.indexOf(connectionConfiguration);
            int updatedIndex = -1;

            if(index == -1) {
                connections.add(connectionConfiguration);
                updatedIndex = connections.size();
            }
            else {
                connections.remove(index);
                connections.add(index, connectionConfiguration);
                updatedIndex = index;
            }

            fireIntervalAdded(this, updatedIndex, updatedIndex);
        }

        public void remove(int index) {
            connections.remove(index);
            fireIntervalRemoved(this, index, index);
        }

        public List<ConnectionConfiguration> getConnectionConfigurations() {
            return connections;
        }

    }

    private static final ConnectionConfiguration DEFAULT_CONNECTION_PREFERRENCES
        = new ConnectionConfiguration("<unamed>","localhost",1521, "xe", "pythia", "");

    private final ConnectionListModel model;
    private final LoginView view;

    private LoginResult result = LoginResult.CANCEL;

    public LoginController() {
        view  = new LoginView((Dialog)null);

        ConnectionConfiguration lastConnectionConfiguration = PreferencesManager.
                getLastConfiguration();

        List<ConnectionConfiguration> savedConnectionConfiguration = PreferencesManager.
                getSavedConnectionConfiguration();

        if(lastConnectionConfiguration == null) {
            updateView(DEFAULT_CONNECTION_PREFERRENCES);
        }
        else {
            updateView(lastConnectionConfiguration);
        }

        model = new ConnectionListModel(savedConnectionConfiguration);

        bindActions();
        bindRenderer();
    }

    public LoginResult showDialog() {
        view.setVisible(true);
        return result;
    }

    private void bindActions() {

        view.getButtonConnect().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleConnectButton();
            }
        });

        view.getButtonAdd().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSaveButton();
            }
        });

        view.getButtonCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancelButton();
            }
        });

        view.getButtonRemoveSavedConnection().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSavedConnection();
            }
        });

        view.getSavedConnectionsList().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int index = view.getSavedConnectionsList().getSelectedIndex();

                    if (index != -1) {
                        updateView(model.getElementAt(index));
                    }
                }
            }
        });

        view.getMenuCreateSchemaScript().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSchemaCreationScript();
            }
        });

        view.getSavedConnectionsList().setModel(model);
        view.getRootPane().setDefaultButton(view.getButtonConnect());
    }

    private void bindRenderer() {
        // prepare port textfield to only allow integer values
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        numberFormat.setGroupingUsed(false);

        NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);

        view.getTextFieldPort().setFormatterFactory(new DefaultFormatterFactory(numberFormatter));

        //set cell renderer for saved connection configurations only the name will be shown
        view.getSavedConnectionsList().setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                ConnectionConfiguration connection = (ConnectionConfiguration) value;

                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText(connection.getConnectionName());

                return this;
            }
        });
    }

    private void handleConnectButton() {
        ConnectionConfiguration connectionConfiguration = getConnectionPreferencesFromView();

        // configurePool connection the connection using the given configuration
        Properties credentials = new Properties();
        credentials.put("user", connectionConfiguration.getUser());
        credentials.put("password", connectionConfiguration.getPassword());

        try {
            ConnectionPoolUtils.configurePool(connectionConfiguration.toConnectionString(), credentials);
        }
        catch (SQLException e) {
            // The connection could not be established
            JOptionPane.showMessageDialog(view, "The connection could not be established.\n"
                    + "The error message is "+ e.toString());


        }

        result = LoginResult.OK;

        PreferencesManager.setLastConfiguration(connectionConfiguration);
        PreferencesManager.savePythiaConfiguration();

        view.dispose();

    }

    private void handleSaveButton() {
        ConnectionConfiguration connectionConfiguration = getConnectionPreferencesFromView();
        model.addOrUpdate(connectionConfiguration);

        PreferencesManager.addOrUpdateSavedConnectionConfiguration(connectionConfiguration);
        PreferencesManager.savePythiaConfiguration();
    }

    private void handleCancelButton() {
        view.dispose();
        result = LoginResult.CANCEL;
    }

    private void removeSavedConnection() {
        int index = view.getSavedConnectionsList().getSelectedIndex();

        ConnectionConfiguration connectionConfiguration = model.getElementAt(index);

        if(index != -1) {
            model.remove(index);
        }

        PreferencesManager.removeSavedConnectionConfiguration(connectionConfiguration);
        PreferencesManager.savePythiaConfiguration();
    }

    private void updateView(ConnectionConfiguration connection) {
        view.getTextFieldConnectionName().setText(connection.getConnectionName());
        view.getTextFieldHost().setText(connection.getHost());
        view.getTextFieldPort().setValue(connection.getPort());
        view.getTextFieldDatabaseName().setText(connection.getDatabaseName());
        view.getTextFieldUser().setText(connection.getUser());
    }

    private ConnectionConfiguration getConnectionPreferencesFromView() {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(
                view.getTextFieldConnectionName().getText(),
                view.getTextFieldHost().getText(),
                (Integer) view.getTextFieldPort().getValue(),
                view.getTextFieldDatabaseName().getText(),
                view.getTextFieldUser().getText(),
                view.getTextFieldPassword().getText());

        return connectionConfiguration;
    }

    private void generateSchemaCreationScript() {
        final String DEFAULT_SCHEMA_NAME = "pythia";

        File destination = FileSelectorUtility.chooseSQLFileToWrite(view,
                "CreatePythiaSchema.sql");

        if (destination != null) {
            PrintWriter output = null;
            try {
                output = new PrintWriter(new FileOutputStream(destination));

                output.println("-- -----------------------------------------------------------------------------");
                output.println("-- Schema creation script generated by Pythia");
                output.println("-- -----------------------------------------------------------------------------");
                output.println("CREATE USER " + DEFAULT_SCHEMA_NAME
                        + " IDENTIFIED BY \"" + DEFAULT_SCHEMA_NAME + "\";");
                output.println("GRANT create session TO " + DEFAULT_SCHEMA_NAME
                        + ";");
                output.println();

                output.println(PrivilegesHelper.createGrantScript(
                        PrivilegesHelper
                                .getMissingObjectPrivileges((List<String>) null),
                        DEFAULT_SCHEMA_NAME));

                JOptionPane.showMessageDialog(view,
                        "Schema creation script generated.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog((Component) null, e);
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        }
    }
}
