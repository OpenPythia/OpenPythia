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
import java.io.*;
import java.math.BigDecimal;
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
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openpythia.dbconnection.ConnectionPoolUtils;
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

    public static boolean saveSnapshot(String snapshotId, File snapshotFile) {
        Snapshot toSave = getSnapshot(snapshotId);

        try (
                OutputStream file = new FileOutputStream(snapshotFile);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);
        ){
            output.writeObject(toSave);
            return true;
        }
        catch(IOException ex){
            return false;
        }
    }

    public static boolean loadSnapshot(File snapshotFile) {
        try(
                InputStream file = new FileInputStream(snapshotFile);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream (buffer);
        ){
            Snapshot loaded = (Snapshot)input.readObject();
            addSnapshot(loaded);
            return true;
        }
        catch(ClassNotFoundException ex){
            return false;
        }
        catch(IOException ex){
            return false;
        }
    }

    public static void takeSnapshot(ProgressListener progressListener) {
        new Thread(new SnapshotTaker(progressListener)).start();
    }

    private static class SnapshotTaker implements Runnable {

        private static String SNAPSHOT_SQL_AREA = "SELECT sql_id, address, inst_id, parsing_schema_name, "
                + "executions, elapsed_time / 1000000, cpu_time / 1000000, buffer_gets, disk_reads, "
                + "concurrency_wait_time / 1000000, cluster_wait_time / 1000000, rows_processed "
                + "FROM gv$sqlarea";

        private ProgressListener progressListener;

        public SnapshotTaker(ProgressListener progressListener) {
            this.progressListener = progressListener;
        }

        @Override
        public void run() {
            progressListener.setStartValue(0);
            progressListener.setEndValue(SQLHelper.getNumberSQLStatements());
            progressListener.setCurrentValue(0);

            Snapshot snapshot = new Snapshot(SQLHelper.getCurrentDBDateTime());

            fillSnapshot(snapshot);

            addSnapshot(snapshot);

            progressListener.informFinished();
        }

        private void fillSnapshot(Snapshot snapshot) {
            Connection connection = ConnectionPoolUtils.getConnectionFromPool();
            try {
                PreparedStatement snapshotStatement = connection
                        .prepareStatement(SNAPSHOT_SQL_AREA);

                ResultSet snapshotResultSet = snapshotStatement.executeQuery();

                int lines = 0;
                if (snapshotResultSet != null) {
                    while (snapshotResultSet.next()) {
                        int columnIndex = 1;
                        String sqlId = snapshotResultSet.getString(columnIndex++);
                        String address = snapshotResultSet.getString(columnIndex++);
                        Integer instanceId = snapshotResultSet.getInt(columnIndex++);

                        String parsingSchema = snapshotResultSet.getString(columnIndex++);
                        BigDecimal executions = snapshotResultSet.getBigDecimal(columnIndex++);
                        BigDecimal elapsedSeconds = snapshotResultSet.getBigDecimal(columnIndex++);
                        BigDecimal cpuSeconds = snapshotResultSet.getBigDecimal(columnIndex++);
                        BigDecimal bufferGets = snapshotResultSet.getBigDecimal(columnIndex++);
                        BigDecimal diskReads = snapshotResultSet.getBigDecimal(columnIndex++);
                        BigDecimal concurrencySeconds = snapshotResultSet.getBigDecimal(columnIndex++);
                        BigDecimal clusterSeconds = snapshotResultSet.getBigDecimal(columnIndex++);
                        BigDecimal rowsProcessed = snapshotResultSet.getBigDecimal(columnIndex++);

                        SQLStatementSnapshot sqlStatementSnapshot = new SQLStatementSnapshot(
                                SQLHelper.getSQLStatement(sqlId, address, parsingSchema, instanceId),
                                instanceId,
                                executions,
                                elapsedSeconds,
                                cpuSeconds,
                                bufferGets,
                                diskReads,
                                concurrencySeconds,
                                clusterSeconds,
                                rowsProcessed);

                        snapshot.addSQLStatementSnapshot(sqlStatementSnapshot);

                        lines++;
                        progressListener.setCurrentValue(lines);
                    }
                }

                snapshotStatement.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                ConnectionPoolUtils.returnConnectionToPool(connection);
            }

        }
    }

}
