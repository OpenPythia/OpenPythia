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

import org.apache.poi.ss.usermodel.*;
import org.openpythia.progress.ProgressListener;
import org.openpythia.utilities.SSUtilities;
import org.openpythia.utilities.deltasql.DeltaSQLStatementSnapshot;
import org.openpythia.utilities.deltasql.DeltaSnapshot;
import org.openpythia.utilities.sql.ExecutionPlan;
import org.openpythia.utilities.sql.ExecutionPlanStep;
import org.openpythia.utilities.sql.SQLHelper;
import org.openpythia.utilities.waitevent.WaitEventForStatementTuple;
import org.openpythia.utilities.waitevent.WaitEventForTimeSpanTuple;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeltaSnapshotWriter {

    private static final int INDEX_ROW_TEMPLATE_DELTA_SQL_STATEMENT = 3;
    private static final int INDEX_ROW_START_SQL_STATEMENTS = 4;
    private static final int INDEX_ROW_SUM_FORMULAS = 2;

    private static final int INDEX_COLUMN_SQL_ID = 25;
    private static final int INDEX_COLUMN_DELTA_ROWS_PROCESSED = 23;
    private static final int INDEX_COLUMN_DELTA_CLUSTER_SECONDS = 21;
    private static final int INDEX_COLUMN_DELTA_CONCURRENCY_SECONDS = 19;
    private static final int INDEX_COLUMN_DELTA_DISK_READS = 17;
    private static final int INDEX_COLUMN_DELTA_BUFFER_GETS = 15;
    private static final int INDEX_COLUMN_DELTA_CPU_SECONDS = 12;
    private static final int INDEX_COLUMN_DELTA_ELAPSED_SECONDS = 9;
    private static final int INDEX_COLUMN_DELTA_EXECUTIONS = 7;
    private static final int INDEX_COLUMN_NUMBER_IDENTICAL_STATEMENTS = 6;
    private static final int INDEX_COLUMN_HAS_WAIT = 5;
    private static final int INDEX_COLUMN_HAS_PLAN = 4;
    private static final int INDEX_COLUMN_SQL_TEXT = 3;
    private static final int INDEX_COLUMN_INSTANCE = 2;
    private static final int INDEX_COLUMN_PARSING_SCHEMA = 1;
    private static final int INDEX_COLUMN_NO = 0;

    private static final int EXECUTION_PLANS_INDEX_ROW_TEMPLATE_STATEMENT_HEADER_ROW = 3;
    private static final int EXECUTION_PLANS_INDEX_ROW_TEMPLATE_CHILD_HEADER_ROW = 4;
    private static final int EXECUTION_PLANS_INDEX_ROW_TEMPLATE_EXECUTION_STEP_ROW = 5;
    private static final int EXECUTION_PLANS_INDEX_START_EXECUTION_PLANS = 6;

    private static final int WAIT_EVENTS_SQL_INDEX_ROW_TEMPLATE_STATEMENT_HEADER_ROW = 2;
    private static final int WAIT_EVENTS_SQL_INDEX_ROW_TEMPLATE_WAIT_EVENT_ROW = 3;
    private static final int WAIT_EVENTS_SQL_INDEX_START_WAIT_EVENTS = 4;

    private static final int WAIT_EVENTS_INDEX_ROW_TEMPLATE_WAIT_EVENT_ROW = 1;
    private static final int WAIT_EVENTS_INDEX_START_WAIT_EVENTS = 2;

    private static final String TEMPLATE_DELTA_V_SQL_AREA_XLSX = "Template_DELTA_V$SQLAREA.xlsx";

    private File destination;
    private DeltaSnapshot deltaSnapshot;
    private boolean moreExecutionPlans;
    private ProgressListener listener;

    private Sheet statementsSheet;
    private Sheet executionPlansSheet;
    private Sheet waitEventsForStatementSheet;
    private Sheet waitEventsForTimeSpanSheet;
    private CellStyle hyperlinkStyle;

    private DeltaSnapshotWriter(File destination, DeltaSnapshot deltaSnapshot,
                                boolean moreExecutionPlans, ProgressListener listener) {
        this.destination = destination;
        this.deltaSnapshot = deltaSnapshot;
        this.moreExecutionPlans = moreExecutionPlans;
        this.listener = listener;
    }

    private boolean saveDeltaSnapshot() {
        try {
            Workbook workbook = WorkbookFactory.create(this.getClass().getResourceAsStream(TEMPLATE_DELTA_V_SQL_AREA_XLSX));
            statementsSheet = workbook.getSheet("Delta V$SQLAREA");
            executionPlansSheet = workbook.getSheet("Execution Plans");
            waitEventsForStatementSheet = workbook.getSheet("Wait Events per SQL Statement");
            waitEventsForTimeSpanSheet = workbook.getSheet("All Wait Events");
            hyperlinkStyle = createHyperlinkStyle(workbook);

            writeDeltaSnapshotStatements();

            List<DeltaSQLStatementSnapshot> worstStatements = getWorstSQLStatements();

            try {
                writeExecutionPlansForWorstStatements(worstStatements);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e);
            }

            try {
                writeWaitEventsForWorstStatements(worstStatements);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e);
            }

            try {
                writeWaitEventsForTimeSpan();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e);
            }

            if (listener != null) {
                listener.informFinished();
            }

            OutputStream outputStream = new FileOutputStream(destination);
            workbook.write(outputStream);
            outputStream.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
            return false;
        }
    }

    private List<DeltaSQLStatementSnapshot> getWorstSQLStatements() {
        List<DeltaSQLStatementSnapshot> worstStatements;
        WorstStatementIdentifier wsi = new WorstStatementIdentifier(deltaSnapshot, moreExecutionPlans);

        // Identify the worst statements
        worstStatements = new ArrayList<>();
        int statementCount = 1;
        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot.getDeltaSqlStatementSnapshots()) {

            if ((moreExecutionPlans && statementCount++ <= 100)
                    || wsi.isAWorstStatement(currentSnapshot)) {
                worstStatements.add(currentSnapshot);
            }
        }
        return worstStatements;
    }

    public static boolean saveDeltaSnapshot(File destination, DeltaSnapshot deltaSnapshot,
                                            boolean moreExecutionPlans, ProgressListener listener) {
        DeltaSnapshotWriter writer = new DeltaSnapshotWriter(destination, deltaSnapshot, moreExecutionPlans, listener);
        return writer.saveDeltaSnapshot();
    }

    private CellStyle createHyperlinkStyle(Workbook workbook) {
        // cell style for hyperlinks
        // by default hyperlinks are blue and underlined
        CellStyle hyperlinkStyle = workbook.createCellStyle();
        hyperlinkStyle.cloneStyleFrom(statementsSheet.getRow(INDEX_ROW_TEMPLATE_DELTA_SQL_STATEMENT).getCell(INDEX_COLUMN_SQL_TEXT).getCellStyle());

        Font hyperlinkFont = workbook.createFont();
        hyperlinkFont.setUnderline(Font.U_SINGLE);
        hyperlinkFont.setColor(IndexedColors.BLUE.getIndex());

        hyperlinkStyle.setFont(hyperlinkFont);

        return hyperlinkStyle;
    }

    private void writeDeltaSnapshotStatements() {
        Row templateRow = statementsSheet.getRow(INDEX_ROW_TEMPLATE_DELTA_SQL_STATEMENT);

        int currentRowIndex = INDEX_ROW_START_SQL_STATEMENTS;
        int currentNumber = 1;
        boolean snapshotContainsMissingBindVariables = false;

        if (listener != null) {
            listener.setMessage("Writing the statements...");
            listener.setStartValue(0);
            listener.setEndValue(deltaSnapshot.getDeltaSqlStatementSnapshots().size());
        }

        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot.getDeltaSqlStatementSnapshots()) {
            if (listener != null) {
                listener.setCurrentValue(currentNumber);
            }

            Row currentRow = SSUtilities.copyRow(statementsSheet, templateRow, currentRowIndex);

            currentRow.getCell(INDEX_COLUMN_NO).setCellValue(currentNumber++);

            currentRow.getCell(INDEX_COLUMN_PARSING_SCHEMA).setCellValue(
                    currentSnapshot.getSqlStatement().getParsingSchema());
            currentRow.getCell(INDEX_COLUMN_INSTANCE).setCellValue(
                    currentSnapshot.getInstanceId());
            // Excel is limited to 32.767 chars per cell
            if (currentSnapshot.getSqlStatement() == null ||
                    currentSnapshot.getSqlStatement().getSqlText() == null) {
                // The snapshot is no longer available in the library cache so there is no way to get it's SQL text
                currentRow.getCell(INDEX_COLUMN_SQL_TEXT).setCellValue("<Statement was swapped out of the library cache.>");
            } else {
                currentRow.getCell(INDEX_COLUMN_SQL_TEXT).setCellValue(currentSnapshot.getSqlStatement().getSqlTextTrimmedForExcel());
            }

            if (currentSnapshot.getDeltaNumberStatements() == null) {
                currentRow.getCell(INDEX_COLUMN_NUMBER_IDENTICAL_STATEMENTS).setCellValue(" ");
            } else {
                currentRow.getCell(INDEX_COLUMN_NUMBER_IDENTICAL_STATEMENTS).setCellValue(
                        currentSnapshot.getDeltaNumberStatements().doubleValue());
                snapshotContainsMissingBindVariables = true;
            }

            currentRow.getCell(INDEX_COLUMN_DELTA_EXECUTIONS).setCellValue(
                    currentSnapshot.getDeltaExecutions().doubleValue());

            currentRow.getCell(INDEX_COLUMN_DELTA_ELAPSED_SECONDS).setCellValue(
                    currentSnapshot.getDeltaElapsedSeconds().doubleValue());
            currentRow.getCell(INDEX_COLUMN_DELTA_CPU_SECONDS).setCellValue(
                    currentSnapshot.getDeltaCpuSeconds().doubleValue());

            currentRow.getCell(INDEX_COLUMN_DELTA_BUFFER_GETS).setCellValue(
                    currentSnapshot.getDeltaBufferGets().doubleValue());
            currentRow.getCell(INDEX_COLUMN_DELTA_DISK_READS).setCellValue(
                    currentSnapshot.getDeltaDiskReads().doubleValue());

            currentRow.getCell(INDEX_COLUMN_DELTA_CONCURRENCY_SECONDS).setCellValue(
                    currentSnapshot.getDeltaConcurrencySeconds().doubleValue());
            currentRow.getCell(INDEX_COLUMN_DELTA_CLUSTER_SECONDS).setCellValue(
                    currentSnapshot.getDeltaClusterSeconds().doubleValue());

            currentRow.getCell(INDEX_COLUMN_DELTA_ROWS_PROCESSED).setCellValue(
                    currentSnapshot.getDeltaRowsProcessed().doubleValue());

            currentRow.getCell(INDEX_COLUMN_SQL_ID).setCellValue(
                    currentSnapshot.getSqlStatement().getSqlId());

            currentRowIndex++;
        }

        if (!snapshotContainsMissingBindVariables) {
            // if there are no statements with missing bind variables this column is not needed.
            statementsSheet.setColumnHidden(INDEX_COLUMN_NUMBER_IDENTICAL_STATEMENTS, true);
        }

        // delete the template row
        SSUtilities.deleteRow(statementsSheet, templateRow);

        // update the formulas in the third row (sum)
        FormulaEvaluator evaluator = statementsSheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        Iterator<Cell> cellIterator = statementsSheet.getRow(INDEX_ROW_SUM_FORMULAS).cellIterator();
        while (cellIterator.hasNext()) {
            Cell currentCell = cellIterator.next();
            if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                evaluator.evaluateFormulaCell(currentCell);
            }
        }
    }

    private void writeExecutionPlansForWorstStatements(List<DeltaSQLStatementSnapshot> worstStatements) {
        Row templateStatementHeaderRow = executionPlansSheet.getRow(EXECUTION_PLANS_INDEX_ROW_TEMPLATE_STATEMENT_HEADER_ROW);
        Row templateChildHeaderRow = executionPlansSheet.getRow(EXECUTION_PLANS_INDEX_ROW_TEMPLATE_CHILD_HEADER_ROW);
        Row templateExecutionStepRow = executionPlansSheet.getRow(EXECUTION_PLANS_INDEX_ROW_TEMPLATE_EXECUTION_STEP_ROW);

        if (listener != null) {
            listener.setMessage("Loading the execution plans...");
        }
        SQLHelper.loadExecutionPlansForStatements(worstStatements, listener);

        // Now write the execution plans into the Excel sheet
        int currentRowIndex = EXECUTION_PLANS_INDEX_START_EXECUTION_PLANS;
        Row currentRow;

        int currentDataSet = 0;

        if (listener != null) {
            listener.setMessage("Writing the execution plans...");
            listener.setStartValue(0);
            listener.setEndValue(worstStatements.size());
        }

        for (DeltaSQLStatementSnapshot currentSnapshot : worstStatements) {

            currentDataSet++;
            if (listener != null) {
                listener.setCurrentValue(currentDataSet);
            }
            // Header for statement
            currentRow = SSUtilities.copyRow(executionPlansSheet, templateStatementHeaderRow, currentRowIndex);

            currentRow.getCell(0).setCellValue(currentSnapshot.getSqlStatement().getSqlId());
            currentRow.getCell(1).setCellValue(currentSnapshot.getSqlStatement().getSqlTextTrimmedForExcel());
            currentRowIndex++;

            // Link from sheet with statements to this execution plan
            Hyperlink link = statementsSheet.getWorkbook().getCreationHelper().createHyperlink(Hyperlink.LINK_DOCUMENT);
            // Later on we will delete three rows at the beginning of the sheet. Prepare the
            // hyperlink to point to the correct cell after the deletion.
            link.setAddress("'" + executionPlansSheet.getSheetName() + "'!A" + (currentRowIndex - 3));
            addLinkFromStatement(currentSnapshot.getSqlStatement().getSqlId(), INDEX_COLUMN_HAS_PLAN, link);

            for (ExecutionPlan currentPlan : currentSnapshot.getSqlStatement().getExecutionPlans()) {
                // Header for Child / Execution Plan
                // There may be more than one execution plans...
                currentRow = SSUtilities.copyRow(executionPlansSheet, templateChildHeaderRow, currentRowIndex);
                currentRow.getCell(0).setCellValue(currentPlan.getInstanceId());
                currentRow.getCell(1).setCellValue(currentPlan.getChildNumber());
                currentRowIndex++;

                currentRowIndex = writeExecutionPlanStepToExcel(
                        templateExecutionStepRow, currentRowIndex,
                        currentPlan.getParentStep());
            }
        }

        // delete the template rows
        SSUtilities.deleteRow(executionPlansSheet, templateStatementHeaderRow);
        SSUtilities.deleteRow(executionPlansSheet, templateChildHeaderRow);
        SSUtilities.deleteRow(executionPlansSheet, templateExecutionStepRow);
    }

    private void addLinkFromStatement(String sqlId, int indexHyperlinkColumn, Hyperlink hyperlinkToCell) {

        int rowIndex = 1;
        while (rowIndex <= statementsSheet.getLastRowNum()
                && !statementsSheet.getRow(rowIndex)
                .getCell(INDEX_COLUMN_SQL_ID).getStringCellValue()
                .equals(sqlId)) {
            rowIndex++;
        }

        if (statementsSheet.getRow(rowIndex).getCell(INDEX_COLUMN_SQL_ID)
                .getStringCellValue().equals(sqlId)) {

            statementsSheet.getRow(rowIndex).getCell(indexHyperlinkColumn).setCellValue("X");
            statementsSheet.getRow(rowIndex).getCell(indexHyperlinkColumn)
                    .setHyperlink(hyperlinkToCell);
            statementsSheet.getRow(rowIndex).getCell(indexHyperlinkColumn)
                    .setCellStyle(hyperlinkStyle);
        }
    }

    private int writeExecutionPlanStepToExcel(Row templateForExecutionStepRow,
                                              int currentRowIndex, ExecutionPlanStep step) {

        int internalCurrentRowIndex = currentRowIndex;

        // Row for current step in the Execution Plan
        Row currentRow = SSUtilities.copyRow(executionPlansSheet,
                templateForExecutionStepRow, currentRowIndex);
        String operation = getIndentionAsString(step.getDepth())
                + step.getOperation();
        if (step.getOptions() != null) {
            operation += " (" + step.getOptions() + ")";
        }
        currentRow.getCell(0).setCellValue(operation);
        if (step.getObjectOwner() != null) {
            currentRow.getCell(1).setCellValue(
                    step.getObjectOwner() + "." + step.getObjectName());
        }
        nullSafeBigDecimalIntoCellWriter(currentRow.getCell(2), step.getCost());
        nullSafeBigDecimalIntoCellWriter(currentRow.getCell(3), step.getCardinality());
        nullSafeBigDecimalIntoCellWriter(currentRow.getCell(4), step.getBytes());
        nullSafeBigDecimalIntoCellWriter(currentRow.getCell(5), step.getCpuCost());
        nullSafeBigDecimalIntoCellWriter(currentRow.getCell(6), step.getIoCost());
        currentRow.getCell(7).setCellValue(step.getAccessPredicates());
        currentRow.getCell(8).setCellValue(step.getFilterPredicates());
        internalCurrentRowIndex++;

        for (ExecutionPlanStep childStep : step.getChildSteps()) {
            internalCurrentRowIndex = writeExecutionPlanStepToExcel(
                    templateForExecutionStepRow, internalCurrentRowIndex,
                    childStep);
        }

        return internalCurrentRowIndex;
    }

    private void writeWaitEventsForWorstStatements(List<DeltaSQLStatementSnapshot> worstStatements) {

        Row templateStatementHeaderRow = waitEventsForStatementSheet.getRow(WAIT_EVENTS_SQL_INDEX_ROW_TEMPLATE_STATEMENT_HEADER_ROW);
        Row templateWaitEventRow = waitEventsForStatementSheet.getRow(WAIT_EVENTS_SQL_INDEX_ROW_TEMPLATE_WAIT_EVENT_ROW);

        Map<DeltaSQLStatementSnapshot, List<WaitEventForStatementTuple>> waitEventsPerStatementMap =
                SQLHelper.loadWaitEventsForStatements(
                        worstStatements,
                        deltaSnapshot.getSnapshotA().getSnapshotTime(),
                        deltaSnapshot.getSnapshotB().getSnapshotTime(),
                        null);

        // Now write the wait events into the Excel sheet
        int currentRowIndex = WAIT_EVENTS_SQL_INDEX_START_WAIT_EVENTS;
        Row currentRow;

        int currentDataSet = 0;

        if (listener != null) {
            listener.setMessage("Writing the wait events...");
            listener.setStartValue(0);
            listener.setEndValue(worstStatements.size());
        }

        for (DeltaSQLStatementSnapshot currentSnapshot : worstStatements) {

            currentDataSet++;
            if (listener != null) {
                listener.setCurrentValue(currentDataSet);
            }

            if (waitEventsPerStatementMap.get(currentSnapshot) == null) {
                // if this worst statement has no wait events associated with, go to the next one
                continue;
            }

            // Header for statement
            currentRow = SSUtilities.copyRow(waitEventsForStatementSheet, templateStatementHeaderRow, currentRowIndex);

            currentRow.getCell(0).setCellValue(currentSnapshot.getSqlStatement().getSqlId());
            currentRow.getCell(1).setCellValue(currentSnapshot.getSqlStatement().getSqlTextTrimmedForExcel());
            currentRowIndex++;

            // Link from sheet with statements to this list of wait events
            Hyperlink link = statementsSheet.getWorkbook().getCreationHelper().createHyperlink(Hyperlink.LINK_DOCUMENT);
            // Later on we will delete two rows at the beginning of the sheet. Prepare the
            // hyperlink to point to the correct cell after the deletion.
            link.setAddress("'" + waitEventsForStatementSheet.getSheetName() + "'!A" + (currentRowIndex - 2));
            addLinkFromStatement(currentSnapshot.getSqlStatement().getSqlId(), INDEX_COLUMN_HAS_WAIT, link);

            for (WaitEventForStatementTuple currentTuple : waitEventsPerStatementMap.get(currentSnapshot)) {
                currentRow = SSUtilities.copyRow(waitEventsForStatementSheet, templateWaitEventRow, currentRowIndex);

                int columnIndex = 0;
                currentRow.getCell(columnIndex++).setCellValue(currentTuple.getWaitEventName());
                currentRow.getCell(columnIndex++).setCellValue(currentTuple.getWaitObjectOwner());
                currentRow.getCell(columnIndex++).setCellValue(currentTuple.getWaitObjectName());
                //noinspection UnusedAssignment
                nullSafeBigDecimalIntoCellWriter(currentRow.getCell(columnIndex++), currentTuple.getWaitedSeconds());
                currentRowIndex++;
            }
        }

        // delete the template rows
        SSUtilities.deleteRow(waitEventsForStatementSheet, templateStatementHeaderRow);
        SSUtilities.deleteRow(waitEventsForStatementSheet, templateWaitEventRow);
    }

    private void writeWaitEventsForTimeSpan() {

        Row templateWaitEventRow = waitEventsForTimeSpanSheet.getRow(WAIT_EVENTS_INDEX_ROW_TEMPLATE_WAIT_EVENT_ROW);

        List<WaitEventForTimeSpanTuple> waitEventsList =
                SQLHelper.loadWaitEventsForTimeSpan(
                        deltaSnapshot.getSnapshotA().getSnapshotTime(),
                        deltaSnapshot.getSnapshotB().getSnapshotTime());

        // Now write the wait events into the Excel sheet
        int currentRowIndex = WAIT_EVENTS_INDEX_START_WAIT_EVENTS;
        Row currentRow;

        for (WaitEventForTimeSpanTuple currentWaitEvent : waitEventsList) {

            // Header for statement
            currentRow = SSUtilities.copyRow(waitEventsForTimeSpanSheet, templateWaitEventRow, currentRowIndex);

            int columnIndex = 0;
            currentRow.getCell(columnIndex++).setCellValue(currentWaitEvent.getWaitEventName());
            currentRow.getCell(columnIndex++).setCellValue(currentWaitEvent.getWaitObjectOwner());
            currentRow.getCell(columnIndex++).setCellValue(currentWaitEvent.getWaitObjectName());
            //noinspection UnusedAssignment
            nullSafeBigDecimalIntoCellWriter(currentRow.getCell(columnIndex++), currentWaitEvent.getWaitedSeconds());

            currentRowIndex++;
        }

        // delete the template row
        SSUtilities.deleteRow(waitEventsForTimeSpanSheet, templateWaitEventRow);
    }

    private void nullSafeBigDecimalIntoCellWriter(Cell cell, BigDecimal value) {
        if (value == null) {
            cell.setCellValue("");
        } else {
            cell.setCellValue(value.doubleValue());
        }
    }

    private String getIndentionAsString(BigDecimal depth) {
        StringBuilder result = new StringBuilder("");

        for (int i = 1; i <= depth.intValue(); i++) {
            result.append("  ");
        }

        return result.toString();
    }

    // Is a statement a "worst statement"?
    // To find out, we need to look at this statement compared to all the
    // statements: A statement gets a worst statement, when it eats up a
    // big amount of runtime, CPU, buffer gets...
    private static class WorstStatementIdentifier {

        private boolean moreExecutionPlans;
        private BigDecimal sumExecutions = BigDecimal.ZERO;
        private BigDecimal sumElapsedSeconds = BigDecimal.ZERO;
        private BigDecimal sumCPUSeconds = BigDecimal.ZERO;
        private BigDecimal sumBufferGets = BigDecimal.ZERO;
        private BigDecimal sumDiskReads = BigDecimal.ZERO;

        public WorstStatementIdentifier(DeltaSnapshot deltaSnapshot, boolean moreExecutionPlans) {
            this.moreExecutionPlans = moreExecutionPlans;

            // Calculate and store some sums to compare against single statements
            for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot.getDeltaSqlStatementSnapshots()) {

                sumExecutions = sumExecutions.add(currentSnapshot.getDeltaExecutions());
                sumElapsedSeconds = sumElapsedSeconds.add(currentSnapshot.getDeltaElapsedSeconds());
                sumCPUSeconds = sumCPUSeconds.add(currentSnapshot.getDeltaCpuSeconds());
                sumBufferGets = sumBufferGets.add(currentSnapshot.getDeltaBufferGets());
                sumDiskReads = sumDiskReads.add(currentSnapshot.getDeltaDiskReads());
            }
        }

        public boolean isAWorstStatement(DeltaSQLStatementSnapshot snapshot) {
            // Implementation is analog to the conditional formatting in the
            // Excel sheet

            // more than 1 % of the total
            float percentThreshold = 1;
            if (moreExecutionPlans) {
                percentThreshold = 0.5f;
            }
            float multiplyFactor = 100.0f / percentThreshold;

            if (snapshot.getDeltaExecutions().multiply(new BigDecimal(multiplyFactor)).compareTo(sumExecutions) > 0) {
                return true;
            }
            if (snapshot.getDeltaElapsedSeconds().multiply(new BigDecimal(multiplyFactor)).compareTo(sumElapsedSeconds) > 0) {
                return true;
            }
            if (snapshot.getDeltaCpuSeconds().multiply(new BigDecimal(multiplyFactor)).compareTo(sumCPUSeconds) > 0) {
                return true;
            }
            if (snapshot.getDeltaBufferGets().multiply(new BigDecimal(multiplyFactor)).compareTo(sumBufferGets) > 0) {
                return true;
            }
            if (snapshot.getDeltaDiskReads().multiply(new BigDecimal(multiplyFactor)).compareTo(sumDiskReads) > 0) {
                return true;
            }
            if (snapshot.getDeltaExecutions().compareTo(BigDecimal.ZERO) > 0 &&
                    snapshot.getDeltaElapsedSeconds().divide(snapshot.getDeltaExecutions(), 1, BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.ONE) >= 0) {
                // average elapsed time at least 1 second
                return true;
            }

            return false;
        }
    }
}
