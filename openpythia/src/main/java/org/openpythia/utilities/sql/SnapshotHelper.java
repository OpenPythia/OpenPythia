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

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import javax.swing.JOptionPane;

import org.openpythia.dbconnection.ConnectionPoolUtils;
import org.openpythia.progress.ProgressListener;
import org.openpythia.utilities.FileSelectorUtility;

public class SnapshotHelper {
    private static List<Boolean> stopAutomatedThread = new ArrayList<>();

    private static SortedMap<String, Snapshot> snapshots = new ConcurrentSkipListMap<>();

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

    private static void addSnapshot(Snapshot snapshot) {
        if (snapshot != null) {
            snapshots.put(snapshot.getSnapshotName(), snapshot);
        }
    }

    public static boolean saveSnapshot(String snapshotId, File snapshotFile) {
        // on automatic snapshot saving, the files are saved under OpenPythia project
        Snapshot toSave = getSnapshot(snapshotId);

        try (
                OutputStream file = new FileOutputStream(snapshotFile);
                OutputStream buffer = new BufferedOutputStream(file);
                // we use GZIP because ZIP is not transparent but needs special calls to run
                OutputStream zipped = new GZIPOutputStream(buffer);
                ObjectOutput output = new ObjectOutputStream(zipped);
        ){
            output.writeObject(toSave);
            return true;
        }
        catch(IOException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean loadSnapshot(File snapshotFile) {
        // default are compressed snapshots
        return loadSnapshot(snapshotFile, true);
    }

    private static boolean loadSnapshot(File snapshotFile, boolean compressed) {
        if (compressed) {
            try (
                    InputStream file = new FileInputStream(snapshotFile);
                    InputStream buffer = new BufferedInputStream(file);
                    InputStream unzipped = new GZIPInputStream(buffer);
                    ObjectInput input = new ObjectInputStream(unzipped);
            ) {
                integrateSnapshotIntoDataStructures(input);

                return true;
            } catch (ZipException ex) {
                // this is an old snapshot not using the compression
                return loadSnapshot(snapshotFile, false);
            } catch (ClassNotFoundException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        } else {
            try (
                    InputStream file = new FileInputStream(snapshotFile);
                    InputStream buffer = new BufferedInputStream(file);
                    ObjectInput input = new ObjectInputStream(buffer);
            ) {
                integrateSnapshotIntoDataStructures(input);

                return true;
            } catch (ClassNotFoundException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }
        }
    }

    private static void integrateSnapshotIntoDataStructures(ObjectInput input) throws IOException, ClassNotFoundException {
        Snapshot loaded = (Snapshot) input.readObject();

        for (SQLStatementSnapshot currentStatement : loaded.getSqlStatementSnapshots()) {
            currentStatement.updateSQLStatement(SQLHelper.getRegisterSQLStatement(currentStatement.getSqlStatement()));
        }
        addSnapshot(loaded);
    }

    public static void takeSnapshot(ProgressListener progressListener, String connectionName, boolean isAutomated, Integer timeInterval, int threadIndex) {

        // every new started thread for taking snapshots automatically after a time interval is controlled using an ArrayList of booleans.
        // When starting a new thread, a new index is sent to this method, so the value at this index controls the thread.
        // The first element in the Array is reserved for the snapshots that are not automatically taken. That is why, there is the need to check
        // and to set the value of the first item when "take snapshot" button is clicked or when the first thread of automatic snapshots is started

        if (threadIndex == 0)
        {
            if (stopAutomatedThread.isEmpty()) {
                stopAutomatedThread.add(0, false);
                }
        } else {
            if (stopAutomatedThread.isEmpty() && threadIndex == 1) {
                stopAutomatedThread.add(0, false);
            }
            stopAutomatedThread.add(threadIndex, false);
        }
        new Thread(new SnapshotTaker(progressListener, connectionName, isAutomated, timeInterval, threadIndex)).start();
    }

    public static void stopAutomatedSnapshots(int threadIndex){
        stopAutomatedThread.set(threadIndex, true);
        Thread.currentThread().interrupt();
    }

    private static class SnapshotTaker implements Runnable {

        // From Oracle 11gR2 on there is a column FULL_TEXT which could be used to directly load
        // the SQL text. This would make the logic of OpenPythia much simpler. This approach was
        // taken but failed because of the very bad performance: For some reasons Oracle takes
        // much time to access the full text via thia way. In addition it took very long to load
        // the text to the client. So for the moment this way will not be taken.
        private static String SNAPSHOT_SQL_AREA = String.format("SELECT sql_id, inst_id, parsing_schema_name, executions, elapsed_time / 1000000, cpu_time / 1000000, buffer_gets, disk_reads, concurrency_wait_time / 1000000, cluster_wait_time / 1000000, rows_processed FROM gv$sqlarea");

        private ProgressListener progressListener;
        private String connectionName;
        private boolean isAutomated;
        private long timeInterval;
        private int automatedThreadIndex;

        public SnapshotTaker(ProgressListener progressListener, String connectionName, boolean isAutomated, Integer timeInterval, int threadIndex) {
            this.progressListener = progressListener;
            this.connectionName = connectionName;
            this.isAutomated = isAutomated;
            this.timeInterval = timeInterval;
            this.automatedThreadIndex = threadIndex;
        }

            @Override
            public void run() {
            if(isAutomated) {
                try {
                    do {
                        // prepare the snapshot
                        Snapshot snapshot =  prepareSnapshot();
                        String snapshotFileName = FileSelectorUtility.suggestedFileNameForSnapshotID(snapshot.getSnapshotName());

                        // add it to the snapshots list is UI
                        addSnapshot(snapshot);
                        File snapshotFile = new File(snapshotFileName);

                        // save it under OpenPyhtia project
                        saveSnapshot(snapshot.getSnapshotName(), snapshotFile);

                        // if the "stop automatic snapshots" button was clicked, set timeInterval to 0
                        if(stopAutomatedThread.get(automatedThreadIndex)) {
                            timeInterval = 0;
                        }
                        Thread.sleep(timeInterval * 60000);
                        // do this until the item with the index "automatedThreadIndex" is set to true
                    } while (!stopAutomatedThread.get(automatedThreadIndex));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                prepareSnapshot();
            }
        }

        private Snapshot prepareSnapshot() {
            progressListener.setStartValue(0);
            progressListener.setEndValue(SQLHelper.getNumberSQLStatementsInLibraryCache());
            progressListener.setCurrentValue(0);

            Snapshot snapshot = new Snapshot(SQLHelper.getCurrentDBDateTime(), connectionName);

            fillSnapshot(snapshot);

            addSnapshot(snapshot);
            progressListener.informFinished();
            return snapshot;
        }

        private void fillSnapshot(Snapshot snapshot) {

            Connection connection = ConnectionPoolUtils.getConnectionFromPool();
            try {
                PreparedStatement snapshotStatement;
                  snapshotStatement = connection.prepareStatement(SNAPSHOT_SQL_AREA);

                ResultSet snapshotResultSet = snapshotStatement.executeQuery();

                int lines = 0;
                if (snapshotResultSet != null) {
                    while (snapshotResultSet.next()) {
                        int columnIndex = 1;
                        String sqlId = snapshotResultSet.getString(columnIndex++);
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
                                SQLHelper.getSQLStatement(sqlId, parsingSchema, instanceId),
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
