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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.openpythia.progress.FinishedListener;
import org.openpythia.progress.ProgressController;
import org.openpythia.progress.ProgressListener;
import org.openpythia.utilities.FileSelectorUtility;
import org.openpythia.utilities.deltasql.DeltaSQLStatementSnapshot;
import org.openpythia.utilities.deltasql.DeltaSnapshot;
import org.openpythia.utilities.sql.SQLHelper;
import org.openpythia.utilities.sql.SQLStatement;
import org.openpythia.utilities.sql.SnapshotHelper;

public class WorstStatementsDetailController implements FinishedListener {

    private Frame owner;
    private String connectionName;
    private WorstStatementsDetailView view;

    private static File lastSnapshotPath;
    private static File lastExcelExportPath;

    private DeltaSnapshot deltaSnapshot = null;

    private boolean dialogBlocked = false;

    public WorstStatementsDetailController(Frame owner, String connectionName) {
        this.owner = owner;
        this.connectionName = connectionName;

        view = new WorstStatementsDetailView();

        fillScenarios();

        bindActions();

        deltaSnapshot = new DeltaSnapshot();
        showDeltaSnapshot(deltaSnapshot);
    }

    private void fillScenarios() {
        DefaultListModel snapshotListModel = new DefaultListModel();
        for (String snapshotId : SnapshotHelper.getAllSnapshotIds()) {
            snapshotListModel.addElement(snapshotId);
        }
        view.getListSnapshots().setModel(snapshotListModel);
    }

    private void bindActions() {
        view.getBtnTakeSnapshot().addActionListener(e -> takeSnapshot());

        // initially the option to take snapshots automatically is not selected, there is why, the time interval and the buton to activate the automated snapshots are disabled
        view.getComboTimeInterval().setEnabled(false);
        view.getBtnTakeAutomatedSnapshots().setEnabled(false);

        // enable/disable options depending on checkbox isSelected value
        view.getCheckboxAutomatedSnapshots().addActionListener(e -> {
                    if (view.getCheckboxAutomatedSnapshots().isSelected()) {
                        view.getComboTimeInterval().setEnabled(true);
                        view.getBtnTakeAutomatedSnapshots().setEnabled(true);
                        view.getBtnTakeAutomatedSnapshots().addActionListener(y -> takeAutomatedSnapshotsAndSave(view.getComboTimeInterval().toString()));
                    } else {
                        view.getComboTimeInterval().setEnabled(false);
                        view.getBtnTakeAutomatedSnapshots().setEnabled(false);
                    }
                });
        view.getBtnSaveSnapshot().addActionListener(e -> saveSnapshot());
        view.getBtnLoadSnapshot().addActionListener(e -> loadSnapshot());
        view.getBtnCompareSnapshots().addActionListener(e -> compareSnapshot());

        view.getBtnExportExcel().addActionListener(e -> exportDeltaToExcel());
        view.getBtnExportExcel().setEnabled(false);
        view.getListSnapshots().addListSelectionListener(
                e -> setGUIElementsToCorrectState());
        view.getBtnCompareSnapshots().setEnabled(false);
        view.getCbCondenseInstances().setEnabled(false);
        view.getCbCondenseMissingBindvariables().setEnabled(false);

        view.getBtnSaveSnapshot().setEnabled(false);
        view.getBtnLoadSnapshot().setEnabled(true);
    }

    private void takeSnapshot() {
        ProgressController controller = new ProgressController(owner, this,
                "Taking Snapshot...",
                "Pythia is taking a snapshot of the library cache.");
        SnapshotHelper.takeSnapshot(controller, connectionName, false, null);

        SQLHelper.startSQLTextLoader();
    }
    private void takeAutomatedSnapshotsAndSave(String timeInterval) {

        // do this after timeInterval minutes

            ProgressController controller = new ProgressController(owner, this,
                    "Taking Snapshot...",
                    "Pythia is taking a snapshot of the library cache.");
            SnapshotHelper.takeSnapshot(controller, connectionName, true, (Integer) view.getComboTimeInterval().getSelectedItem());

            SQLHelper.startSQLTextLoader();
    }

    private void saveSnapshot() {
        int numberSelectedSnapshots = view.getListSnapshots().getSelectedIndices().length;
        if (numberSelectedSnapshots != 1) {
            JOptionPane.showMessageDialog(view, "Select the snapshot to save.", "Save Snapshot", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String snapshotIdToSave = (String) view.getListSnapshots().getSelectedValuesList().get(0);

        File snapshotFile = FileSelectorUtility.chooseSnapshotFileToWrite(view, lastSnapshotPath, snapshotIdToSave);
        if (snapshotFile != null) {
            // store the directory for next call
            lastSnapshotPath = snapshotFile.getParentFile();
            if (SnapshotHelper.saveSnapshot(snapshotIdToSave, snapshotFile)) {
                JOptionPane.showMessageDialog(view, "Snapshot successfully written.", "Snapshot Export", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Snapshot could not be written.", "Snapshot Export", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void loadSnapshot() {
        File snapshotFile = FileSelectorUtility.chooseSnapshotFileToRead(view, lastSnapshotPath);
        if (snapshotFile != null) {
            // store the directory for next call
            lastSnapshotPath = snapshotFile.getParentFile();
            if (SnapshotHelper.loadSnapshot(snapshotFile)) {
                fillScenarios();
            } else {
                JOptionPane.showMessageDialog(view, "Snapshot could not be loaded.", "Snapshot Import", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void compareSnapshot() {
        dialogBlocked = true;
        setGUIElementsToCorrectState();
        int numberSelectedSnapshots = view.getListSnapshots().getSelectedIndices().length;
        if (numberSelectedSnapshots < 2) {
            // at least two snapshots have to be selected
            dialogBlocked = false;
            setGUIElementsToCorrectState();
            return;
        }

        // clean display during calculation
        deltaSnapshot = new DeltaSnapshot();
        showDeltaSnapshot(deltaSnapshot);
        view.getBtnExportExcel().setEnabled(false);

        String oldSnapshotId = (String) view.getListSnapshots().getSelectedValuesList().get(0);
        String newSnapshotId = (String) view.getListSnapshots().getSelectedValuesList().get(numberSelectedSnapshots - 1);

        boolean condenseInstances = view.getCbCondenseInstances().isSelected();
        boolean condenseMissingBindVariables = view.getCbCondenseMissingBindvariables().isSelected();

        // TODO add a progress indicator
        ProgressListener listener = new ProgressController(owner, this,
                "Compare Snapshots",
                "Comparing Snaphots...");
        new Thread(new SnapshotComparator(oldSnapshotId, newSnapshotId,
                condenseInstances, condenseMissingBindVariables,
                listener)).start();
    }

    private void setGUIElementsToCorrectState() {
        if (dialogBlocked) {
            view.getBtnTakeSnapshot().setEnabled(false);
            view.getBtnSaveSnapshot().setEnabled(false);
            view.getBtnLoadSnapshot().setEnabled(false);
            view.getBtnCompareSnapshots().setEnabled(false);

            view.getCbCondenseInstances().setEnabled(false);
            view.getCbCondenseMissingBindvariables().setEnabled(false);

            view.getCbMoreExecutionPlans().setEnabled(false);
            view.getBtnExportExcel().setEnabled(false);
        } else {
            // when the dialog is not blocked the user can always take and load snapshots
            view.getBtnTakeSnapshot().setEnabled(true);
            view.getCheckboxAutomatedSnapshots().setEnabled(true);
            view.getBtnLoadSnapshot().setEnabled(true);

            // some of the other buttons rely on the number of selected elements
            if (view.getListSnapshots().getSelectedIndices().length > 1) {
                view.getBtnCompareSnapshots().setEnabled(true);
                view.getCbCondenseInstances().setEnabled(true);
                view.getCbCondenseMissingBindvariables().setEnabled(true);

                view.getBtnSaveSnapshot().setEnabled(false);
            } else {
                view.getBtnCompareSnapshots().setEnabled(false);
                view.getCbCondenseInstances().setEnabled(false);
                view.getCbCondenseMissingBindvariables().setEnabled(false);

                if (view.getListSnapshots().getSelectedIndices().length == 1) {
                    view.getBtnSaveSnapshot().setEnabled(true);
                }
            }

            if (deltaSnapshot != null &&
                    deltaSnapshot.getDeltaSqlStatementSnapshots().size() > 0) {
                // if there is a delta snapshot it can be exported
                view.getCbMoreExecutionPlans().setEnabled(true);
                view.getBtnExportExcel().setEnabled(true);
            }
        }
    }

    private class SnapshotComparator implements Runnable {

        private String oldSnapshotId;
        private String newSnapshotId;
        private boolean condenseInstances;
        private boolean condenseMissingBindVariables;
        private ProgressListener listener;

        // public
        SnapshotComparator(String oldSnapshotId, String newSnapshotId,
                                  boolean condenseInstances, boolean condenseMissingBindVariables,
                                  ProgressListener listener) {
            this.oldSnapshotId = oldSnapshotId;
            this.newSnapshotId = newSnapshotId;
            this.condenseInstances = condenseInstances;
            this.condenseMissingBindVariables = condenseMissingBindVariables;
            this.listener = listener;
        }

        @Override
        public void run() {
            deltaSnapshot = new DeltaSnapshot(
                    SnapshotHelper.getSnapshot(oldSnapshotId),
                    SnapshotHelper.getSnapshot(newSnapshotId),
                    condenseInstances,
                    condenseMissingBindVariables,
                    listener);

            // make sure all the SQL text is loaded
            List<SQLStatement> sqlStatements = new ArrayList<>();
            for (DeltaSQLStatementSnapshot statement : deltaSnapshot.getDeltaSqlStatementSnapshots()) {
                if (statement.getSqlStatement().getSqlText() == null) {
                    sqlStatements.add(statement.getSqlStatement());
                }
            }

            SQLHelper.loadSQLTextForStatements(sqlStatements, listener);

            SwingUtilities.invokeLater(() -> {
                showDeltaSnapshot(deltaSnapshot);
                dialogBlocked = false;
                setGUIElementsToCorrectState();
            });
        }
    }

    private void showDeltaSnapshot(DeltaSnapshot deltaSnapshot) {
        view.getTfSnapshotA().setText(deltaSnapshot.getSnapshotA().getSnapshotId());
        view.getTfSnapshotB().setText(deltaSnapshot.getSnapshotB().getSnapshotId());
        if (deltaSnapshot.getDeltaSqlStatementSnapshots().size() > 0) {
            view.getTfNumberStatements().setText(String.format("%,d", deltaSnapshot.getDeltaSqlStatementSnapshots().size()));
        } else {
            view.getTfNumberStatements().setText("");
        }

        view.getTableDeltaSQLStatements().setModel(new DeltaSnapshotTableModel(deltaSnapshot));
    }

    private void exportDeltaToExcel() {
        File excelFile = FileSelectorUtility.chooseExcelFileToWrite(view, lastExcelExportPath);
        if (excelFile != null) {
            // store the directory for next call
            lastExcelExportPath = excelFile.getParentFile();

            dialogBlocked = true;
            setGUIElementsToCorrectState();

            ProgressListener listener = new ProgressController(owner, this,
                    "Export to Excel",
                    "Export the delta snapshot to Excel..");
            new Thread(new DeltaToExcelExporter(excelFile,
                    deltaSnapshot,
                    view.getCbMoreExecutionPlans().isSelected(),
                    listener)).start();
        }
    }

    private class DeltaToExcelExporter implements Runnable {

        private File excelFile;
        private DeltaSnapshot deltaSnapshot;
        private boolean loadMoreExecutionPlans;
        private ProgressListener listener;

        // public
        DeltaToExcelExporter(File excelFile, DeltaSnapshot deltaSnapshot,
                                    boolean loadMoreExecutionPlans, ProgressListener listener) {
            this.excelFile = excelFile;
            this.deltaSnapshot = deltaSnapshot;
            this.loadMoreExecutionPlans = loadMoreExecutionPlans;
            this.listener = listener;
        }

        @Override
        public void run() {
            boolean success = DeltaSnapshotWriter.saveDeltaSnapshot(
                    excelFile,
                    deltaSnapshot,
                    loadMoreExecutionPlans,
                    listener);

            SwingUtilities.invokeLater(() -> {
                dialogBlocked = false;
                setGUIElementsToCorrectState();
            });

            if (!success){
                // if the file could not be written there is no point in opening it
                return;
            }

            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(view,
                    "Excel file successfully written. Open File?",
                    "Excel Export",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE)) {

                try {
                    Desktop.getDesktop().open(excelFile);
                } catch (IOException e) {
                    // nothing to do
                }
            }
        }
    }

    //public
    JPanel getDetailView() {
        return view;
    }

    @Override
    public void informFinished() {
        // called when
        // - taking the snapshot is done
        // - the SQL text for all statements is loaded
        fillScenarios();
    }
}
