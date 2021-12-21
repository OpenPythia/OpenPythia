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

    public JCheckBox getCheckboxAutomatedSnapshots() {return checkboxAutomatedSnapshots;}

    public JComboBox getComboTimeInterval() {
        return comboTimeInterval;
    }

    public JButton getBtnTakeAutomatedSnapshots() {
        return btnTakeAutomatedSnapshots;
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

    public JButton getBtnSaveSnapshot() {
        return btnSaveSnapshot;
    }

    public JButton getBtnLoadSnapshot() {
        return btnLoadSnapshot;
    }

    public JCheckBox getCbMoreExecutionPlans() {
        return cbMoreExecutionPlans;
    }

    public JCheckBox getCbCondenseInstances() {
        return cbCondenseInstances;
    }

    public JCheckBox getCbCondenseMissingBindvariables() {
        return cbCondenseMissingBindvariables;
    }

    public JTable getTableDeltaSQLStatements() {
        return tableDeltaSQLStatements;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        listSnapshots = new JList();
        btnTakeSnapshot = new JButton();
        checkboxAutomatedSnapshots = new JCheckBox();
        comboTimeInterval = new JComboBox();
        btnTakeAutomatedSnapshots = new JButton();
        btnSaveSnapshot = new JButton();
        btnLoadSnapshot = new JButton();
        separator2 = compFactory.createSeparator("Compare Snapshots");
        cbCondenseInstances = new JCheckBox();
        cbCondenseMissingBindvariables = new JCheckBox();
        btnCompareSnapshots = new JButton();
        separator1 = compFactory.createSeparator("Delta");
        label2 = new JLabel();
        tfSnapshotA = new JTextField();
        cbMoreExecutionPlans = new JCheckBox();
        label3 = new JLabel();
        tfSnapshotB = new JTextField();
        btnExportExcel = new JButton();
        label4 = new JLabel();
        tfNumberStatements = new JTextField();
        scrollPane2 = new JScrollPane();
        tableDeltaSQLStatements = new JTable();

        //======== this ========
        setLayout(new FormLayout(
            "$lcgap, default, $lcgap, 100dlu, $lcgap, 131dlu, $lcgap, default:grow, $lcgap",
            "$lgap, top:default, $lgap, 10dlu, 11*($lgap, default), $lgap, 21dlu, fill:default:grow, 2*($lgap)"));

        //---- label1 ----
        label1.setText("Snapshots");
        add(label1, CC.xy(2, 2));

        //======== scrollPane1 ========
        {

            //---- listSnapshots ----
            listSnapshots.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            scrollPane1.setViewportView(listSnapshots);
        }
        add(scrollPane1, CC.xywh(4, 2, 1, 19));

        //---- btnTakeSnapshot ----
        btnTakeSnapshot.setText("Take Snapshot");
        add(btnTakeSnapshot, CC.xy(6, 2));

        //---- checkboxAutomatedSnapshots ----
        checkboxAutomatedSnapshots.setText("Take automated Snapshots");
        add(checkboxAutomatedSnapshots, CC.xy(6, 4));

        //---- comboTimeInterval ----
        comboTimeInterval.setToolTipText("snapshots time interval");
        add(comboTimeInterval, CC.xy(6, 6));
        comboTimeInterval.addItem(30);
        comboTimeInterval.addItem(60);
        comboTimeInterval.addItem(90);
        comboTimeInterval.addItem(120);

        //---- btnTakeAutomatedSnapshots ----
        btnTakeAutomatedSnapshots.setText("Set Automated Snapshots");
        add(btnTakeAutomatedSnapshots, CC.xy(6, 8));

        //---- btnSaveSnapshot ----
        btnSaveSnapshot.setText("Save Snapshot");
        add(btnSaveSnapshot, CC.xy(6, 10));

        //---- btnLoadSnapshot ----
        btnLoadSnapshot.setText("Load Snapshot");
        add(btnLoadSnapshot, CC.xy(6, 12));
        add(separator2, CC.xywh(6, 14, 2, 1));

        //---- cbCondenseInstances ----
        cbCondenseInstances.setText("Condense Instances");
        add(cbCondenseInstances, CC.xy(6, 16));

        //---- cbCondenseMissingBindvariables ----
        cbCondenseMissingBindvariables.setText("Condense Missing Bindvariables");
        add(cbCondenseMissingBindvariables, CC.xy(6, 18));

        //---- btnCompareSnapshots ----
        btnCompareSnapshots.setText("Compare Snapshots...");
        add(btnCompareSnapshots, CC.xy(6, 20));
        add(separator1, CC.xywh(2, 22, 7, 1));

        //---- label2 ----
        label2.setText("Snapshot A");
        add(label2, CC.xy(2, 24));

        //---- tfSnapshotA ----
        tfSnapshotA.setEditable(false);
        add(tfSnapshotA, CC.xy(4, 24));

        //---- cbMoreExecutionPlans ----
        cbMoreExecutionPlans.setText("Load More Executation Plans");
        add(cbMoreExecutionPlans, CC.xy(6, 24));

        //---- label3 ----
        label3.setText("Snapshot B");
        add(label3, CC.xy(2, 26));

        //---- tfSnapshotB ----
        tfSnapshotB.setEditable(false);
        add(tfSnapshotB, CC.xy(4, 26));

        //---- btnExportExcel ----
        btnExportExcel.setText("Export to Excel");
        add(btnExportExcel, CC.xy(6, 26));

        //---- label4 ----
        label4.setText("Number Statements");
        add(label4, CC.xy(2, 28));

        //---- tfNumberStatements ----
        tfNumberStatements.setEditable(false);
        add(tfNumberStatements, CC.xy(4, 28));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(tableDeltaSQLStatements);
        }
        add(scrollPane2, CC.xywh(2, 29, 7, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JList listSnapshots;
    private JButton btnTakeSnapshot;
    private JCheckBox checkboxAutomatedSnapshots;
    private JComboBox comboTimeInterval;
    private JButton btnTakeAutomatedSnapshots;
    private JButton btnSaveSnapshot;
    private JButton btnLoadSnapshot;
    private JComponent separator2;
    private JCheckBox cbCondenseInstances;
    private JCheckBox cbCondenseMissingBindvariables;
    private JButton btnCompareSnapshots;
    private JComponent separator1;
    private JLabel label2;
    private JTextField tfSnapshotA;
    private JCheckBox cbMoreExecutionPlans;
    private JLabel label3;
    private JTextField tfSnapshotB;
    private JButton btnExportExcel;
    private JLabel label4;
    private JTextField tfNumberStatements;
    private JScrollPane scrollPane2;
    private JTable tableDeltaSQLStatements;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
