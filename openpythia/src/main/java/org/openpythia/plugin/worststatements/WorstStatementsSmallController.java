package org.openpythia.plugin.worststatements;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openpythia.dbconnection.ConnectionPool;
import org.openpythia.plugin.MainDialog;
import org.openpythia.plugin.PythiaPluginController;
import org.openpythia.utilities.sql.SQLHelper;

public class WorstStatementsSmallController implements PythiaPluginController {

    private static String ELAPSED_TIME_TOP20 = "SELECT SUM(elapsed_time) "
            + "FROM (SELECT elapsed_time FROM v$sqlarea "
            + "WHERE rownum <= 20 " + "ORDER BY elapsed_time DESC)";

    private static String ELAPSED_TIME_TOTAL = "SELECT SUM(elapsed_time) "
            + "FROM v$sqlarea ";

    private MainDialog mainDialog;

    private Updater updater;

    private WorstStatementsSmallView smallView;
    private WorstStatementsDetailController detailController;

    public WorstStatementsSmallController(Frame owner, MainDialog mainDialog,
            ConnectionPool connectionPool) {
        this.mainDialog = mainDialog;

        updater = new Updater(connectionPool);

        detailController = new WorstStatementsDetailController(owner,
                connectionPool);

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

        private ConnectionPool connectionPool;

        public Updater(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
        }

        @Override
        public void run() {
            int numberSQLStatements = SQLHelper
                    .getNumberSQLStatements(connectionPool);
            float ratioTop20 = getRatioTop20();

            updateView(numberSQLStatements, ratioTop20);
        }

        private float getRatioTop20() {
            int elpasedTimeTop20 = 0;
            int elapsedTimeTotal = 1;

            Connection connection = connectionPool.getConnection();
            try {
                PreparedStatement elapsedTop20Statement = connection
                        .prepareStatement(ELAPSED_TIME_TOP20);

                ResultSet elapsedTop20ResultSet = elapsedTop20Statement
                        .executeQuery();

                if (elapsedTop20ResultSet != null) {
                    while (elapsedTop20ResultSet.next()) {
                        elpasedTimeTop20 = elapsedTop20ResultSet.getInt(1);
                    }
                }

                elapsedTop20Statement.close();

                PreparedStatement elapsedTotalStatement = connection
                        .prepareStatement(ELAPSED_TIME_TOTAL);

                ResultSet elapsedTotalResultSet = elapsedTotalStatement
                        .executeQuery();

                if (elapsedTotalResultSet != null) {
                    while (elapsedTotalResultSet.next()) {
                        elapsedTimeTotal = elapsedTotalResultSet.getInt(1);
                    }
                }

                elapsedTotalStatement.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog((Component) null, e);
            } finally {
                connectionPool.giveConnectionBack(connection);
            }
            return (float) elpasedTimeTop20 / (float) elapsedTimeTotal;
        }

        private void updateView(int numberSQLStatements, float ratioTop20) {
            smallView.getTfTotalNumber().setText(
                    String.valueOf(numberSQLStatements));

            smallView.getTfElapsedTop20().setText(
                    String.format("%6.2f", ratioTop20 * 100));
        }
    }
}
