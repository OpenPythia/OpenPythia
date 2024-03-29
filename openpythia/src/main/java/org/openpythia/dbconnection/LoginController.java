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
import org.openpythia.preferences.ConnectionTypeEnum;
import org.openpythia.preferences.PreferencesManager;
import org.openpythia.schemaprivileges.PrivilegesHelper;
import org.openpythia.utilities.FileSelectorUtility;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
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

    private String connectionName;

    public static enum LoginResult {
        OK, CANCEL
    }

    private static class ConnectionListModel extends AbstractListModel {

        private static final long serialVersionUID = -3562953203956378314L;
        private List<ConnectionConfiguration> connections;

        public ConnectionListModel(List<ConnectionConfiguration> connections) {
            this.connections = new LinkedList<>(connections);
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

            if (index == -1) {
                connections.add(connectionConfiguration);
                updatedIndex = connections.size();
            } else {
                connections.remove(index);
                connections.add(index, connectionConfiguration);
                updatedIndex = index;
            }

            Collections.sort(connections, new ConnectionConfigurationComparator());

            fireIntervalAdded(this, updatedIndex, updatedIndex);
        }

        public void remove(int index) {
            connections.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }

    private static final ConnectionConfiguration DEFAULT_CONNECTION_PREFERENCES
            = new ConnectionConfiguration("<unnamed>", "localhost", 1521,
            ConnectionTypeEnum.SID, "xe", "", "",
            "pythia", "");

    private final ConnectionListModel model;
    private final LoginView view;

    private LoginResult result = LoginResult.CANCEL;

    public LoginController() {
        view = new LoginView((Dialog) null);

        ConnectionConfiguration lastConnectionConfiguration = PreferencesManager.getLastConfiguration();

        List<ConnectionConfiguration> savedConnectionConfiguration = PreferencesManager.getSavedConnectionConfiguration();
        Collections.sort(savedConnectionConfiguration, new ConnectionConfigurationComparator());


        if (lastConnectionConfiguration == null) {
            updateView(DEFAULT_CONNECTION_PREFERENCES);
        } else {
            updateView(lastConnectionConfiguration);
        }

        model = new ConnectionListModel(savedConnectionConfiguration);

        bindActions();
        bindRenderer();
    }

    public LoginResult showDialog() {
        view.setLocationRelativeTo(null);
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

            private static final long serialVersionUID = -8268524243585527841L;

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

            connectionName = view.getTextFieldConnectionName().getText();
            result = LoginResult.OK;

            PreferencesManager.setLastConfiguration(connectionConfiguration);
            PreferencesManager.savePythiaConfiguration();

            view.dispose();
        } catch (SQLException e) {
            // The connection could not be established
            JOptionPane.showMessageDialog(view, "The connection could not be established.\n"
                    + "The error message is " + e.toString());
        }
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

        if (index != -1) {
            model.remove(index);
        }

        PreferencesManager.removeSavedConnectionConfiguration(connectionConfiguration);
        PreferencesManager.savePythiaConfiguration();
    }

    private void updateView(ConnectionConfiguration connection) {
        view.getTextFieldConnectionName().setText(connection.getConnectionName());
        view.getTextFieldHost().setText(connection.getHost());
        view.getTextFieldPort().setValue(connection.getPort());
        if (connection.getConnectionTypeEnum() == null) {
            // default is SID
            view.getRbSID().setSelected(true);
        } else {
            switch (connection.getConnectionTypeEnum()) {
                case SID:
                    view.getRbSID().setSelected(true);
                    break;
                case ServiceName:
                    view.getRbServiceName().setSelected(true);
                    break;
                case TNSName:
                    view.getRbTnsName().setSelected(true);
                    break;
            }
        }
        view.getTextFieldSID().setText(connection.getSid());
        view.getTextFieldServiceName().setText(connection.getServiceName());
        view.getTextFieldTnsName().setText(connection.getTnsName());
        view.getTextFieldUser().setText(connection.getUser());
    }

    private ConnectionConfiguration getConnectionPreferencesFromView() {
        ConnectionTypeEnum connectionType;
        if (view.getRbSID().isSelected()) {
            connectionType = ConnectionTypeEnum.SID;
        } else if (view.getRbServiceName().isSelected()) {
            connectionType = ConnectionTypeEnum.ServiceName;
        } else {
            connectionType = ConnectionTypeEnum.TNSName;
        }

        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(
                view.getTextFieldConnectionName().getText(),
                view.getTextFieldHost().getText(),
                (Integer) view.getTextFieldPort().getValue(),
                connectionType,
                view.getTextFieldSID().getText(),
                view.getTextFieldServiceName().getText(),
                view.getTextFieldTnsName().getText(),
                view.getTextFieldUser().getText(),
                String.valueOf(view.getTextFieldPassword().getPassword()));

        return connectionConfiguration;
    }

    public String getConnectionName() {
        return connectionName;
    }

    private void generateSchemaCreationScript() {
        final String DEFAULT_SCHEMA_NAME = "pythia";

        File destination = FileSelectorUtility.chooseSQLFileToWrite(view, null, "CreatePythiaSchema.sql");

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
                        PrivilegesHelper.getMissingObjectPrivileges(null),
                        DEFAULT_SCHEMA_NAME));

                output.println();
                output.println("-- -----------------------------------------------------------------------------");
                output.println("-- Optional: To enable further analysis of schema, grant the SELECT_CATALOG_ROLE");
                output.println("--           to the user.");
                output.println("-- -----------------------------------------------------------------------------");
                output.println("-- GRANT select_catalog_role TO " + DEFAULT_SCHEMA_NAME + ";");

                JOptionPane.showMessageDialog(view, "Schema creation script generated.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        }
    }
}
