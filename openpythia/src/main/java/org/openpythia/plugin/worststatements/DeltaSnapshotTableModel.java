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

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.openpythia.utilities.deltasql.DeltaSnapshot;

class DeltaSnapshotTableModel extends AbstractTableModel implements TableModel {

    private DeltaSnapshot deltaSnaphot;

    private final String[] columnNames = { "SQL", "Executions",
            "Elapsed Seconds", "CPU Seconds", "Buffer Gets", "Disk Reads",
            "Rows Processed" };

    public DeltaSnapshotTableModel(DeltaSnapshot deltaSnapshot) {
        this.deltaSnaphot = deltaSnapshot;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return deltaSnaphot.getDeltaSqlStatementSnapshots().size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return Integer.class;
            case 3:
                return Integer.class;
            case 4:
                return Integer.class;
            case 5:
                return Integer.class;
            case 6:
                return Integer.class;
            default:
                return super.getColumnClass(columnIndex);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return deltaSnaphot.getDeltaSqlStatementSnapshots()
                        .get(rowIndex).getSqlStatement().getSqlText();
            case 1:
                return deltaSnaphot.getDeltaSqlStatementSnapshots()
                        .get(rowIndex).getDeltaExecutions();
            case 2:
                return deltaSnaphot.getDeltaSqlStatementSnapshots()
                        .get(rowIndex).getDeltaElapsedSeconds();
            case 3:
                return deltaSnaphot.getDeltaSqlStatementSnapshots()
                        .get(rowIndex).getDeltaCpuSeconds();
            case 4:
                return deltaSnaphot.getDeltaSqlStatementSnapshots()
                        .get(rowIndex).getDeltaBufferGets();
            case 5:
                return deltaSnaphot.getDeltaSqlStatementSnapshots()
                        .get(rowIndex).getDeltaDiskReads();
            case 6:
                return deltaSnaphot.getDeltaSqlStatementSnapshots()
                        .get(rowIndex).getDeltaRowsProcessed();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        // no column can be edited
    }
}