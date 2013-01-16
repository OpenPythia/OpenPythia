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
package org.openpythia.utilities.sql;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.swing.JOptionPane;

import org.openpythia.dbconnection.ConnectionPool;
import org.openpythia.progress.ProgressListener;

public class SnapshotHelper {

    private static SortedMap<String, Snapshot> snapshots = new ConcurrentSkipListMap<String, Snapshot>();

    public static Set<String> getAllSnapshotIds() {
        return snapshots.keySet();
    }

    public static Snapshot getSnapshot(String snapshotId) {
        if (snapshots.containsKey(snapshotId)) {
            return snapshots.get(snapshotId);
        } else {
            return null;
        }
    }

    public static void addSnapshot(Snapshot snapshot) {
        if (snapshot != null) {
            snapshots.put(snapshot.getSnapshotId(), snapshot);
        }
    }

    public static void takeSnapshot(ConnectionPool connectionPool, ProgressListener progressListener) {
        new Thread(new SnapshotTaker(connectionPool, progressListener)).start();
    }

    private static class SnapshotTaker implements Runnable {

        private static String SNAPSHOT_SQL_AREA = "SELECT sql_id, address, parsing_schema_name, "
                + "executions, elapsed_time / 1000000, cpu_time / 1000000, buffer_gets, disk_reads, rows_processed "
                + "FROM v$sqlarea";

        private ConnectionPool connectionPool;
        private ProgressListener progressListener;

        public SnapshotTaker(ConnectionPool connectionPool, ProgressListener progressListener) {
            this.connectionPool = connectionPool;
            this.progressListener = progressListener;
        }

        @Override
        public void run() {
            progressListener.setStartValue(0);
            progressListener.setEndValue(SQLHelper.getNumberSQLStatements(connectionPool));
            progressListener.setCurrentValue(0);

            Date snapshotDate = SQLHelper.getCurrentDBDateTime(connectionPool);
            Calendar snapshotCalendar = new GregorianCalendar();
            snapshotCalendar.setTime(snapshotDate);
            String snapshotID = snapshotCalendar.get(Calendar.YEAR) + "."
                    + fillLeadingZero(snapshotCalendar.get(Calendar.MONTH)) + "."
                    + fillLeadingZero(snapshotCalendar.get(Calendar.DAY_OF_MONTH)) + " "
                    + fillLeadingZero(snapshotCalendar.get(Calendar.HOUR_OF_DAY)) + ":"
                    + fillLeadingZero(snapshotCalendar.get(Calendar.MINUTE)) + ":"
                    + fillLeadingZero(snapshotCalendar.get(Calendar.SECOND));

            Snapshot snapshot = new Snapshot(snapshotID);

            fillSnapshot(snapshot);

            addSnapshot(snapshot);
            
            progressListener.informFinished();
        }
        
        private String fillLeadingZero(int value) {
            if (value >= 10) {
                return String.valueOf(value);
            } else {
                return "0" + String.valueOf(value);
            }
        }

        private void fillSnapshot(Snapshot snapshot) {
            Connection connection = connectionPool.getConnection();
            try {
                PreparedStatement snapshotStatement = connection
                        .prepareStatement(SNAPSHOT_SQL_AREA);

                ResultSet snapshotResultSet = snapshotStatement.executeQuery();

                int lines = 0;
                if (snapshotResultSet != null) {
                    while (snapshotResultSet.next()) {
                        String sqlId = snapshotResultSet.getString(1);
                        String address = snapshotResultSet.getString(2);
                        String parsingSchema = snapshotResultSet.getString(3);
                        int executions = snapshotResultSet.getInt(4);
                        int elapsedSeconds = snapshotResultSet.getInt(5);
                        int cpuSeconds = snapshotResultSet.getInt(6);
                        int bufferGets = snapshotResultSet.getInt(7);
                        int diskReads = snapshotResultSet.getInt(8);
                        int rowsProcessed = snapshotResultSet.getInt(9);

                        SQLStatementSnapshot sqlStatementSnapshot = new SQLStatementSnapshot(
                                SQLHelper.getSQLStatement(sqlId, address,
                                        parsingSchema), executions,
                                elapsedSeconds, cpuSeconds, bufferGets,
                                diskReads, rowsProcessed);

                        snapshot.addSQLStatementSnapshot(sqlStatementSnapshot);
                        
                        lines++;
                        progressListener.setCurrentValue(lines);
                    }
                }

                snapshotStatement.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog((Component) null, e);
            } finally {
                connectionPool.giveConnectionBack(connection);
            }

        }
    }

}
