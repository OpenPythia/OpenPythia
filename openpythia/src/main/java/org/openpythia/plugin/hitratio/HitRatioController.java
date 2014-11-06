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
package org.openpythia.plugin.hitratio;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openpythia.dbconnection.ConnectionPoolUtils;
import org.openpythia.plugin.PythiaPluginController;

public class HitRatioController implements PythiaPluginController {

    private static String BUFFER_CACHE_SQL_STATEMENT = "SELECT ( 1 - (( phy_reads - phy_direct ) / ( blk_gets + con_gets - phy_direct ))) "
            + "FROM ( SELECT SUM( DECODE( name, 'db block gets', VALUE, 0 ) ) blk_gets, "
            + "              SUM( DECODE( name, 'consistent gets', VALUE, 0 ) ) con_gets, "
            + "              SUM( DECODE( name, 'physical reads', VALUE, 0 ) ) phy_reads, "
            + "              SUM( DECODE( name, "
            + "                             'db block gets', 0, "
            + "                             'consistent gets', 0, "
            + "                             'physical reads', 0, "
            + "                             VALUE ) ) phy_direct "
            + "         FROM v$sysstat "
            + "        WHERE name IN( 'db block gets', 'consistent gets', 'physical reads' ) "
            + "           OR name LIKE( 'physical reads direct%' ) )";

    private static String LIBRARY_CACHE_SQL_STATEMENT = "SELECT SUM( pins - reloads ) / SUM( pins ) "
            + "FROM v$librarycache";

    private Updater updater;

    private HitRatioSmallView smallView;

    public HitRatioController() {

        updater = new Updater();

        smallView = new HitRatioSmallView();

        // initially fill the small view
        updateSmallView();
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
            float bufferCacheHitRatio = getBufferCacheHitRatio();
            float libraryCacheHitRatio = getLibraryCacheHitRatio();

            updateView(bufferCacheHitRatio, libraryCacheHitRatio);
        }

        private float getBufferCacheHitRatio() {
            float result = 0;

            Connection connection = ConnectionPoolUtils.getConnectionFromPool();
            try {
                PreparedStatement bufferCacheStatement = connection
                        .prepareStatement(BUFFER_CACHE_SQL_STATEMENT);

                ResultSet bufferCacheResultSet = bufferCacheStatement
                        .executeQuery();

                if (bufferCacheResultSet != null) {
                    while (bufferCacheResultSet.next()) {
                        result = bufferCacheResultSet.getFloat(1);
                    }
                }

                bufferCacheStatement.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                ConnectionPoolUtils.returnConnectionToPool(connection);
            }
            return result;
        }

        private float getLibraryCacheHitRatio() {
            float result = 0;

            Connection connection = ConnectionPoolUtils.getConnectionFromPool();
            try {
                PreparedStatement libraryCacheStatement = connection
                        .prepareStatement(LIBRARY_CACHE_SQL_STATEMENT);

                ResultSet libraryCacheResultSet = libraryCacheStatement
                        .executeQuery();

                if (libraryCacheResultSet != null) {
                    while (libraryCacheResultSet.next()) {
                        result = libraryCacheResultSet.getFloat(1);
                    }
                }

                libraryCacheStatement.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog((Component) null, e);
            } finally {
                ConnectionPoolUtils.returnConnectionToPool(connection);
            }
            return result;
        }

        private void updateView(float bufferCacheHitRatio,
                float libraryCacheHitRatio) {
            smallView.getTfBufferCacheHitRatio().setText(
                    String.format("%6.2f", bufferCacheHitRatio * 100));
            if (bufferCacheHitRatio >= 0.97) {
                smallView.getLblIconBufferCacheHitRatio().setIcon(
                        new ImageIcon(getClass().getResource(
                                "/circle-green-24-ns.png")));
            } else if (bufferCacheHitRatio >= 0.92) {
                smallView.getLblIconBufferCacheHitRatio().setIcon(
                        new ImageIcon(getClass().getResource(
                                "/circle-yellow-24-ns.png")));
            } else {
                smallView.getLblIconBufferCacheHitRatio().setIcon(
                        new ImageIcon(getClass().getResource(
                                "/circle-red-24-ns.png")));
            }

            smallView.getTfLibraryCacheHitRatio().setText(
                    String.format("%6.2f", libraryCacheHitRatio * 100));
            if (libraryCacheHitRatio >= 0.99) {
                smallView.getLblIconLibraryCacheHitRatio().setIcon(
                        new ImageIcon(getClass().getResource(
                                "/circle-green-24-ns.png")));
            } else if (libraryCacheHitRatio >= 0.97) {
                smallView.getLblIconLibraryCacheHitRatio().setIcon(
                        new ImageIcon(getClass().getResource(
                                "/circle-yellow-24-ns.png")));
            } else {
                smallView.getLblIconLibraryCacheHitRatio().setIcon(
                        new ImageIcon(getClass().getResource(
                                "/circle-red-24-ns.png")));
            }
        }
    }
}
