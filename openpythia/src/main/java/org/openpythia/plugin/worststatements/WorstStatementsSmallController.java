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
package org.openpythia.plugin.worststatements;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openpythia.dbconnection.ConnectionPoolUtils;
import org.openpythia.plugin.MainDialog;
import org.openpythia.plugin.PythiaPluginController;
import org.openpythia.utilities.sql.SQLHelper;

public class WorstStatementsSmallController implements PythiaPluginController {

    private static String ELAPSED_TIME_TOP20 = "SELECT SUM(elapsed_time) "
            + "FROM (SELECT elapsed_time FROM gv$sqlarea "
            + "WHERE rownum <= 20 " + "ORDER BY elapsed_time DESC)";

    private static String ELAPSED_TIME_TOTAL = "SELECT SUM(elapsed_time) "
            + "FROM gv$sqlarea ";

    private MainDialog mainDialog;

    private Updater updater;

    private WorstStatementsSmallView smallView;
    private WorstStatementsDetailController detailController;

    public WorstStatementsSmallController(Frame owner, MainDialog mainDialog) {
        this.mainDialog = mainDialog;

        updater = new Updater();

        detailController = new WorstStatementsDetailController(owner);

        smallView = new WorstStatementsSmallView();

        bindActions();

        // initially fill the small view
        updateSmallView();
    }

    private void bindActions() {
        smallView.getBtnShowDetails().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDetailView();
            }
        });
    }

    private void showDetailView() {
        mainDialog.showDetailView(detailController.getDetailView());
    }

    @Override
    public JPanel getSmallView() {
        return smallView;
    }

    public void updateSmallView() {
        new Thread(updater).start();
    }

    private class Updater implements Runnable {

        @Override
        public void run() {
            int numberSQLStatements = SQLHelper.getNumberSQLStatements();
            float ratioTop20 = getRatioTop20();

            updateView(numberSQLStatements, ratioTop20);
        }

        private float getRatioTop20() {
            BigDecimal elapsedTimeTop20 = new BigDecimal(0);
            BigDecimal elapsedTimeTotal = new BigDecimal(1);

            Connection connection = ConnectionPoolUtils.getConnectionFromPool();
            try {
                PreparedStatement elapsedTop20Statement = connection
                        .prepareStatement(ELAPSED_TIME_TOP20);

                ResultSet elapsedTop20ResultSet = elapsedTop20Statement
                        .executeQuery();

                if (elapsedTop20ResultSet != null) {
                    while (elapsedTop20ResultSet.next()) {
                        elapsedTimeTop20 = elapsedTop20ResultSet.getBigDecimal(1);
                    }
                }

                elapsedTop20Statement.close();

                PreparedStatement elapsedTotalStatement = connection.prepareStatement(ELAPSED_TIME_TOTAL);

                ResultSet elapsedTotalResultSet = elapsedTotalStatement.executeQuery();

                if (elapsedTotalResultSet != null) {
                    while (elapsedTotalResultSet.next()) {
                        elapsedTimeTotal = elapsedTotalResultSet.getBigDecimal(1);
                    }
                }

                elapsedTotalStatement.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog((Component) null, e);
            } finally {
                ConnectionPoolUtils.returnConnectionToPool(connection);
            }
            return elapsedTimeTop20.divide(elapsedTimeTotal, RoundingMode.HALF_UP).floatValue();
        }

        private void updateView(int numberSQLStatements, float ratioTop20) {
            smallView.getTfTotalNumber().setText(
                    String.valueOf(numberSQLStatements));

            smallView.getTfElapsedTop20().setText(
                    String.format("%6.2f", ratioTop20 * 100));
        }
    }
}
