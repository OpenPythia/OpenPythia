/*
 * Created by JFormDesigner on Mon Jun 06 11:34:58 CEST 2011
 */

package org.openpythia.plugin.worststatements;

import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Andreas Rothmann
 */
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

    public JButton getBtnCompareSnapshot() {
        return btnCompareSnapshot;
    }

    public JTextField getTfSnapshotA() {
        return tfSnapshotA;
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

    public JButton getBtnExportExcel() {
        return btnExportExcel;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        label1 = new JLabel();
        scrollPane1 = new JScrollPane();
        listSnapshots = new JList();
        btnTakeSnapshot = new JButton();
        btnCompareSnapshot = new JButton();
        separator1 = compFactory.createSeparator("Delta");
        label2 = new JLabel();
        tfSnapshotA = new JTextField();
        btnExportExcel = new JButton();
        label3 = new JLabel();
        tfSnapshotB = new JTextField();
        label4 = new JLabel();
        tfNumberStatements = new JTextField();
        scrollPane2 = new JScrollPane();
        tableDeltaSQLStatements = new JTable();

        //======== this ========
        setLayout(new FormLayout(
            "$lcgap, default, $lcgap, 100dlu, $lcgap, default, $lcgap, default:grow, $lcgap",
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

        //---- btnCompareSnapshot ----
        btnCompareSnapshot.setText("Compare Snapshots...");
        add(btnCompareSnapshot, CC.xy(6, 4));
        add(separator1, CC.xywh(2, 10, 7, 1));

        //---- label2 ----
        label2.setText("Snapshot A");
        add(label2, CC.xy(2, 12));

        //---- tfSnapshotA ----
        tfSnapshotA.setEditable(false);
        add(tfSnapshotA, CC.xy(4, 12));

        //---- btnExportExcel ----
        btnExportExcel.setText("Export to Excel");
        add(btnExportExcel, CC.xy(6, 12));

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
        add(scrollPane2, CC.xywh(2, 18, 8, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel label1;
    private JScrollPane scrollPane1;
    private JList listSnapshots;
    private JButton btnTakeSnapshot;
    private JButton btnCompareSnapshot;
    private JComponent separator1;
    private JLabel label2;
    private JTextField tfSnapshotA;
    private JButton btnExportExcel;
    private JLabel label3;
    private JTextField tfSnapshotB;
    private JLabel label4;
    private JTextField tfNumberStatements;
    private JScrollPane scrollPane2;
    private JTable tableDeltaSQLStatements;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
