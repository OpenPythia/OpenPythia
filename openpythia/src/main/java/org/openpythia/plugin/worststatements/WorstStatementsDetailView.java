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

import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
public class WorstStatementsDetailView extends JPanel {
    public WorstStatementsDetailView() {
        initComponents();
    }

    public JList getListSnapshots() {
        return listSnapshots;
    }

    public JButton getBtnTakeSnapshot() {
        return btnTakeSnapshot;
    }

    public JButton getBtnCompareSnapshots() {
        return btnCompareSnapshots;
    }

    public JTextField getTfSnapshotA() {
        return tfSnapshotA;
    }

    public JButton getBtnExportExcel() {
        return btnExportExcel;
    }

    public JTextField getTfSnapshotB() {
        return tfSnapshotB;
    }

    public JTextField getTfNumberStatements() {
        return tfNumberStatements;
    }

    public JTable getTableDeltaSQLStatements() {
        return tableDeltaSQLStatements;
    }

    public JButton getBtnSaveSnapshot() {
        return btnSaveSnapshot;
    }

    public JButton getBtnLoadSnapshot() {
        return btnLoadSnapshot;
    }

    public JButton getBtnCompareSnapshotsCondensed() {
        return btnCompareSnapshotsCondensed;
    }

    public JCheckBox getCbMoreExecutionPlans() {
        return cbMoreExecutionPlans;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        listSnapshots = new JList();
        btnTakeSnapshot = new JButton();
        btnSaveSnapshot = new JButton();
        btnLoadSnapshot = new JButton();
        btnCompareSnapshots = new JButton();
        btnCompareSnapshotsCondensed = new JButton();
        separator1 = compFactory.createSeparator("Delta");
        label2 = new JLabel();
        tfSnapshotA = new JTextField();
        btnExportExcel = new JButton();
        cbMoreExecutionPlans = new JCheckBox();
        label3 = new JLabel();
        tfSnapshotB = new JTextField();
        label4 = new JLabel();
        tfNumberStatements = new JTextField();
        scrollPane2 = new JScrollPane();
        tableDeltaSQLStatements = new JTable();

        //======== this ========
        setLayout(new FormLayout(
            "$lcgap, default, $lcgap, 100dlu, 2*($lcgap, default), $lcgap, default:grow, $lcgap",
            "$lgap, top:default, 7*($lgap, default), $lgap, fill:default:grow, $lgap"));

        //---- label1 ----
        label1.setText("Snapshots");
        add(label1, CC.xy(2, 2));

        //======== scrollPane1 ========
        {

            //---- listSnapshots ----
            listSnapshots.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            scrollPane1.setViewportView(listSnapshots);
        }
        add(scrollPane1, CC.xywh(4, 2, 1, 7));

        //---- btnTakeSnapshot ----
        btnTakeSnapshot.setText("Take Snapshot");
        add(btnTakeSnapshot, CC.xy(6, 2));

        //---- btnSaveSnapshot ----
        btnSaveSnapshot.setText("Save Snapshot");
        add(btnSaveSnapshot, CC.xy(6, 4));

        //---- btnLoadSnapshot ----
        btnLoadSnapshot.setText("Load Snapshot");
        add(btnLoadSnapshot, CC.xy(6, 6));

        //---- btnCompareSnapshots ----
        btnCompareSnapshots.setText("Compare Snapshots...");
        add(btnCompareSnapshots, CC.xy(6, 8));

        //---- btnCompareSnapshotsCondensed ----
        btnCompareSnapshotsCondensed.setText("Compare Snapshots Condensed...");
        add(btnCompareSnapshotsCondensed, CC.xy(8, 8));
        add(separator1, CC.xywh(2, 10, 9, 1));

        //---- label2 ----
        label2.setText("Snapshot A");
        add(label2, CC.xy(2, 12));

        //---- tfSnapshotA ----
        tfSnapshotA.setEditable(false);
        add(tfSnapshotA, CC.xy(4, 12));

        //---- btnExportExcel ----
        btnExportExcel.setText("Export to Excel");
        add(btnExportExcel, CC.xy(6, 12));

        //---- cbMoreExecutionPlans ----
        cbMoreExecutionPlans.setText("Load More Executation Plans");
        add(cbMoreExecutionPlans, CC.xy(8, 12));

        //---- label3 ----
        label3.setText("Snapshot B");
        add(label3, CC.xy(2, 14));

        //---- tfSnapshotB ----
        tfSnapshotB.setEditable(false);
        add(tfSnapshotB, CC.xy(4, 14));

        //---- label4 ----
        label4.setText("Number Statements");
        add(label4, CC.xy(2, 16));

        //---- tfNumberStatements ----
        tfNumberStatements.setEditable(false);
        add(tfNumberStatements, CC.xy(4, 16));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(tableDeltaSQLStatements);
        }
        add(scrollPane2, CC.xywh(2, 18, 10, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JList listSnapshots;
    private JButton btnTakeSnapshot;
    private JButton btnSaveSnapshot;
    private JButton btnLoadSnapshot;
    private JButton btnCompareSnapshots;
    private JButton btnCompareSnapshotsCondensed;
    private JComponent separator1;
    private JLabel label2;
    private JTextField tfSnapshotA;
    private JButton btnExportExcel;
    private JCheckBox cbMoreExecutionPlans;
    private JLabel label3;
    private JTextField tfSnapshotB;
    private JLabel label4;
    private JTextField tfNumberStatements;
    private JScrollPane scrollPane2;
    private JTable tableDeltaSQLStatements;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
