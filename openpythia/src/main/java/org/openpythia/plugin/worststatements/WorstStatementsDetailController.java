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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
    private WorstStatementsDetailView view;

    private DeltaSnapshot deltaSnapshot = null;

    public WorstStatementsDetailController(Frame owner) {
        this.owner = owner;

        view = new WorstStatementsDetailView();

        fillScenarios();

        bindActions();
    }

    private void fillScenarios() {
        DefaultListModel snapshotListModel = new DefaultListModel();
        for (String snapshotId : SnapshotHelper.getAllSnapshotIds()) {
            snapshotListModel.addElement(snapshotId);
        }
        view.getListSnapshots().setModel(snapshotListModel);
    }

    private void bindActions() {
        view.getBtnTakeSnapshot().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                takeSnapshot();
            }
        });
        view.getBtnCompareSnapshot().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareSnapshot();
            }
        });
        view.getBtnExportExcel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportDeltaToExcel();
            }
        });
        view.getBtnExportExcel().setEnabled(false);
        view.getListSnapshots().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (view.getListSnapshots().getSelectedIndices().length > 1) {
                            view.getBtnCompareSnapshot().setEnabled(true);
                        } else {
                            view.getBtnCompareSnapshot().setEnabled(false);
                        }
                    }
                });
        view.getBtnCompareSnapshot().setEnabled(false);
    }

    private void takeSnapshot() {
        ProgressController controller = new ProgressController(owner, this,
                "Taking Snapshot...",
                "Pythia is taking a snapshot of the library cache.");
        SnapshotHelper.takeSnapshot(controller);

        SQLHelper.startSQLTextLoader();
    }

    private void compareSnapshot() {
        int numberSelectedSnapshots = view.getListSnapshots()
                .getSelectedIndices().length;
        if (numberSelectedSnapshots < 2) {
            // at least two snapshots have to be selected
            return;
        }
        String oldSnapshotId = (String) view.getListSnapshots()
                .getSelectedValues()[0];
        String newSnapshotId = (String) view.getListSnapshots()
                .getSelectedValues()[numberSelectedSnapshots - 1];
        deltaSnapshot = new DeltaSnapshot(
                SnapshotHelper.getSnapshot(oldSnapshotId),
                SnapshotHelper.getSnapshot(newSnapshotId));
        // make sure all the SQL text is loaded
        List<SQLStatement> sqlStatements = new ArrayList<SQLStatement>();
        for (DeltaSQLStatementSnapshot statement : deltaSnapshot
                .getDeltaSqlStatementSnapshots()) {
            if (statement.getSqlStatement().getSqlText() == null) {
                sqlStatements.add(statement.getSqlStatement());
            }
        }

        if (sqlStatements.size() <= 100) {
            // no progress bar for just one bunch of statements
            SQLHelper.loadSQLTextForStatements(sqlStatements,null);
        } else {
            ProgressListener listener = new ProgressController(owner, this,
                    "Load SQL Text",
                    "Loading SQL text for the statements in the snapshot.");
            new Thread(new SQLTextLoader(sqlStatements, listener)).start();
        }

        showDeltaSnapshot(deltaSnapshot);
        view.getBtnExportExcel().setEnabled(true);
    }

    private class SQLTextLoader implements Runnable {

        private List<SQLStatement> statementsToLoad;
        private ProgressListener progressListener;

        public SQLTextLoader(List<SQLStatement> statementsToLoad,
                ProgressListener progressListener) {
            this.statementsToLoad = statementsToLoad;
            this.progressListener = progressListener;
        }

        @Override
        public void run() {
            SQLHelper.loadSQLTextForStatements(statementsToLoad, progressListener);
        }

    }

    private void showDeltaSnapshot(DeltaSnapshot deltaSnapshot) {
        view.getTfSnapshotA().setText(
                deltaSnapshot.getSnapshotA().getSnapshotId());
        view.getTfSnapshotB().setText(
                deltaSnapshot.getSnapshotB().getSnapshotId());
        view.getTfNumberStatements().setText(
                String.valueOf(deltaSnapshot.getDeltaSqlStatementSnapshots()
                        .size()));

        view.getTableDeltaSQLStatements().setModel(
                new DeltaSnapshotTableModel(deltaSnapshot));
    }

    private void exportDeltaToExcel() {
        File excelFile = FileSelectorUtility.chooseExcelFileToWrite(view);
        if (excelFile != null) {
            DeltaSnapshotWriter.saveDeltaSnapshot(excelFile,deltaSnapshot);
        }
    }

    public JPanel getDetailView() {
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
