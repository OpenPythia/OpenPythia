package org.openpythia.batch;

import org.openpythia.dbconnection.ConnectionPoolUtils;
import org.openpythia.dbconnection.JDBCHandler;
import org.openpythia.progress.ProgressListener;
import org.openpythia.utilities.FileSelectorUtility;
import org.openpythia.utilities.sql.SQLHelper;
import org.openpythia.utilities.sql.SQLStatement;
import org.openpythia.utilities.sql.SQLStatementSnapshot;
import org.openpythia.utilities.sql.SnapshotHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BatchTakeSnapshot {

    private DBConnectionInformation dbConnectionInformation;

    public BatchTakeSnapshot(DBConnectionInformation dbConnectionInformation) {
        this.dbConnectionInformation = dbConnectionInformation;
    }

    public void takeSnapshot(boolean loadSQLText) {
        if (dbConnectionInformation.getHost() == null ||
                dbConnectionInformation.getPort() == null ||
                (dbConnectionInformation.getSid() == null && dbConnectionInformation.getServiceName() == null && dbConnectionInformation.getTnsName() == null) ||
                dbConnectionInformation.getUser() == null ||
                dbConnectionInformation.getPassword() == null) {
            System.out.println("There are parameters missing. Please consult the documentation.");
            return;
        }

        if (dbConnectionInformation.getJdbcPath() != null) {
            if (!JDBCHandler.makeJDBCDriverAvailableFrom(dbConnectionInformation.getJdbcPath())) {
                System.out.println(String.format("There is no JDBC driver available at the given location '%s'.",
                        dbConnectionInformation.getJdbcPath()));
                return;
            }
        } else {
            if (!JDBCHandler.isJDBCDriverAvailable()) {
                System.out.println("There is no JDBC driver available - neither in the classpath nor configured.");
                return;
            }
        }

        if (!BatchHelper.initializeConnectionPool(dbConnectionInformation)) {
            return;
        }

        BatchProgressListener listener = new BatchProgressListener();
        SnapshotHelper.takeSnapshot(listener);
        listener.waitForFinished();

        String theOnlySnapshotId = null;
        for(String currentSnapshotId : SnapshotHelper.getAllSnapshotIds()) {
            theOnlySnapshotId = currentSnapshotId;
        }

        if (theOnlySnapshotId != null) {
            List<SQLStatement> statementsInSnapshot = new ArrayList<>();
            for (SQLStatementSnapshot sqlStatementSnapshot : SnapshotHelper.getSnapshot(theOnlySnapshotId).getSqlStatementSnapshots()) {
                statementsInSnapshot.add(sqlStatementSnapshot.getSqlStatement());
            }
            if (loadSQLText) {
                SQLHelper.loadSQLTextForStatements(statementsInSnapshot, null);
            }

            String outputFileName = FileSelectorUtility.suggestedFileNameForSnapshotID(theOnlySnapshotId);
            if (dbConnectionInformation.getFilePrefix() != null) {
                outputFileName = dbConnectionInformation.getFilePrefix() + outputFileName;
            }
            File outputFile = new File(outputFileName);

            if (dbConnectionInformation.getFilePath() != null) {
                File outputDirectory = new File(dbConnectionInformation.getFilePath());
                if (!outputDirectory.exists() ||
                        !outputDirectory.isDirectory()) {
                    System.out.println(String.format("Given output directory '%s' is no valid directory.",
                            dbConnectionInformation.getFilePath()));
                    return;
                }
                outputFile = new File(outputDirectory, outputFileName);
            }

            if (SnapshotHelper.saveSnapshot(theOnlySnapshotId, outputFile)) {
                System.out.println(String.format("Snapshot file '%s' written.", outputFile.getAbsolutePath()));
            } else {
                System.out.println(String.format("Snapshot couldn't be written to file '%s'.", outputFile.getAbsolutePath()));
            }
        }
    }

    private class BatchProgressListener implements ProgressListener {

        private boolean finished = false;

        @Override
        public void setStartValue(int startValue) {
            // nothing to implement for the batch
        }

        @Override
        public void setEndValue(int endValue) {
            // nothing to implement for the batch
        }

        @Override
        public void setCurrentValue(int currentValue) {
            // nothing to implement for the batch
        }

        @Override
        public void informFinished() {
            finished = true;
        }

        public void waitForFinished() {
            while (!finished) {
                // sleep for 0,5 seconds
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // we don't care for being interrupted
                }
            }
        }
    }

    public void shutDown() {
        ConnectionPoolUtils.shutdownPool();
    }
}