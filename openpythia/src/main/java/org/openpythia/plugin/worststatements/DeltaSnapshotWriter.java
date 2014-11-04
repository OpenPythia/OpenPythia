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

import java.awt.Component;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openpythia.utilities.SSUtilities;
import org.openpythia.utilities.deltasql.DeltaSQLStatementSnapshot;
import org.openpythia.utilities.deltasql.DeltaSnapshot;
import org.openpythia.utilities.sql.ExecutionPlan;
import org.openpythia.utilities.sql.ExecutionPlanStep;
import org.openpythia.utilities.sql.SQLHelper;
import org.openpythia.utilities.sql.SQLStatement;

public class DeltaSnapshotWriter {

    private static final int INDEX_ROW_TEMPLATE_DELTA_SQL_STATEMENT = 3;
    private static final int INDEX_ROW_START_SQL_STATEMENTS = 4;
    private static final int INDEX_ROW_SUM_FORMULAS = 2;

    private static final int INDEX_COLUMN_ADDRESS = 23;
    private static final int INDEX_COLUMN_SQL_ID = 22;
    private static final int INDEX_COLUMN_DELTA_ROWS_PROCESSED = 20;
    private static final int INDEX_COLUMN_DELTA_CLUSTER_SECONDS = 18;
    private static final int INDEX_COLUMN_DELTA_CONCURRENCY_SECONDS = 16;
    private static final int INDEX_COLUMN_DELTA_DISK_READS = 14;
    private static final int INDEX_COLUMN_DELTA_BUFFER_GETS = 12;
    private static final int INDEX_COLUMN_DELTA_CPU_SECONDS = 9;
    private static final int INDEX_COLUMN_DELTA_ELAPSED_SECONDS = 6;
    private static final int INDEX_COLUMN_DELTA_EXECUTIONS = 4;
    private static final int INDEX_COLUMN_SQL_TEXT = 3;
    private static final int INDEX_COLUMN_INSTANCE = 2;
    private static final int INDEX_COLUMN_PARSING_SCHEMA = 1;
    private static final int INDEX_COLUMN_NO = 0;

    private static final int INDEX_ROW_TEMPLATE_STATEMENT_HEADER_ROW = 3;
    private static final int INDEX_ROW_TEMPLATE_CHILD_HEADER_ROW = 4;
    private static final int INDEX_ROW_TEMPLATE_EXECUTION_STEP_ROW = 5;
    private static final int INDEX_START_EXECUTION_PLANS = 6;


    public static final String TEMPLATE_DELTA_V_SQL_AREA_XLSX = "Template_DELTA_V$SQLAREA.xlsx";

    private File destination;
    private DeltaSnapshot deltaSnapshot;
    private boolean moreExecutionPlans;

    private Sheet statementsSheet;
    private Sheet executionPlansSheet;
    private CellStyle hyperlinkStyle;

    private DeltaSnapshotWriter(File destination, DeltaSnapshot deltaSnapshot, boolean moreExecutionPlans) {
        this.destination = destination;
        this.deltaSnapshot = deltaSnapshot;
        this.moreExecutionPlans = moreExecutionPlans;
        this.moreExecutionPlans = moreExecutionPlans;
    }

    private void saveDeltaSnapshot() {
        try {
            Workbook workbook = WorkbookFactory.create(this.getClass().getResourceAsStream(TEMPLATE_DELTA_V_SQL_AREA_XLSX));
            statementsSheet = workbook.getSheet("Delta V$SQLAREA");
            executionPlansSheet = workbook.getSheet("Execution Plans");
            hyperlinkStyle = createHyperlinkStyle(workbook);

            writeDeltaSnapshotStatements();

            writeExecutionPlansForWorstStatements();

            OutputStream outputStream = new FileOutputStream(destination);
            workbook.write(outputStream);
            outputStream.close();

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } catch (InvalidFormatException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        }
    }

    public static void saveDeltaSnapshot(File destination, DeltaSnapshot deltaSnapshot, boolean moreExecutionPlans) {
        DeltaSnapshotWriter writer = new DeltaSnapshotWriter(destination, deltaSnapshot, moreExecutionPlans);
        writer.saveDeltaSnapshot();
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

        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot.getDeltaSqlStatementSnapshots()) {
            Row currentRow = SSUtilities.copyRow(statementsSheet, templateRow, currentRowIndex);

            currentRow.getCell(INDEX_COLUMN_NO).setCellValue(currentNumber++);

            currentRow.getCell(INDEX_COLUMN_PARSING_SCHEMA).setCellValue(
                    currentSnapshot.getSqlStatement().getParsingSchema());
            currentRow.getCell(INDEX_COLUMN_INSTANCE).setCellValue(
                    currentSnapshot.getInstanceId());
            currentRow.getCell(INDEX_COLUMN_SQL_TEXT).setCellValue(
                    currentSnapshot.getSqlStatement().getSqlText());

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
            currentRow.getCell(INDEX_COLUMN_ADDRESS).setCellValue(
                    currentSnapshot.getSqlStatement().getAddress());

            currentRowIndex++;
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

    private void writeExecutionPlansForWorstStatements() {
        Row templateStatementHeaderRow = executionPlansSheet.getRow(INDEX_ROW_TEMPLATE_STATEMENT_HEADER_ROW);
        Row templateChildHeaderRow = executionPlansSheet.getRow(INDEX_ROW_TEMPLATE_CHILD_HEADER_ROW);
        Row templateExecutionStepRow = executionPlansSheet.getRow(INDEX_ROW_TEMPLATE_EXECUTION_STEP_ROW);

        WorstStatementIdentifier wsi = new WorstStatementIdentifier(deltaSnapshot, moreExecutionPlans);

        // Identify the worst statements and load their execution plans
        List<SQLStatement> worstStatements = new ArrayList<>();
        int statementCount = 1;
        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot.getDeltaSqlStatementSnapshots()) {

            if ((moreExecutionPlans && statementCount++ <= 100)
                    || wsi.isAWorstStatement(currentSnapshot)) {
                worstStatements.add(currentSnapshot.getSqlStatement());
            }
        }
        SQLHelper.loadExecutionPlansForStatements(worstStatements, null);

        // Now write the execution plans into the Excel sheet
        int currentRowIndex = INDEX_START_EXECUTION_PLANS;
        Row currentRow;

        statementCount = 1;
        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot.getDeltaSqlStatementSnapshots()) {

            if ((moreExecutionPlans && statementCount++ <= 100)
                    || wsi.isAWorstStatement(currentSnapshot)) {
                // Header for statement
                currentRow = SSUtilities.copyRow(executionPlansSheet, templateStatementHeaderRow, currentRowIndex);

                currentRow.getCell(0).setCellValue(currentSnapshot.getSqlStatement().getSqlId());
                currentRow.getCell(1).setCellValue(currentSnapshot.getSqlStatement().getAddress());
                currentRow.getCell(2).setCellValue(currentSnapshot.getSqlStatement().getSqlText());
                currentRowIndex++;

                // Link from sheet with statements to this execution plan
                Hyperlink link = statementsSheet.getWorkbook().getCreationHelper().createHyperlink(Hyperlink.LINK_DOCUMENT);
                // Later on we will delete three rows at the beginning of the sheet. Prepare the
                // hyperlink to point to the correct cell after the deletion.
                link.setAddress("'" + executionPlansSheet.getSheetName() + "'!A" + (currentRowIndex - 3));
                addLinkFromStatementToExecutionPlans(currentSnapshot.getSqlStatement().getAddress(), link);

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
        }

        // delete the template rows
        SSUtilities.deleteRow(executionPlansSheet, templateStatementHeaderRow);
        SSUtilities.deleteRow(executionPlansSheet, templateChildHeaderRow);
        SSUtilities.deleteRow(executionPlansSheet, templateExecutionStepRow);
    }

    private void addLinkFromStatementToExecutionPlans(String address, Hyperlink hyperlinkToExecutionPlanCell) {

        int rowIndex = 1;
        while (rowIndex <= statementsSheet.getLastRowNum()
                && !statementsSheet.getRow(rowIndex)
                .getCell(INDEX_COLUMN_ADDRESS).getStringCellValue()
                .equals(address)) {
            rowIndex++;
        }

        if (statementsSheet.getRow(rowIndex).getCell(INDEX_COLUMN_ADDRESS)
                .getStringCellValue().equals(address)) {

            statementsSheet.getRow(rowIndex).getCell(INDEX_COLUMN_SQL_TEXT)
                    .setHyperlink(hyperlinkToExecutionPlanCell);
            statementsSheet.getRow(rowIndex).getCell(INDEX_COLUMN_SQL_TEXT)
                    .setCellStyle(hyperlinkStyle);
        }
    }

    private int writeExecutionPlanStepToExcel(Row templateForExcecutionStepRow,
                                              int currentRowIndex, ExecutionPlanStep step) {

        int internalCurrentRowIndex = currentRowIndex;

        // Row for current step in the Execution Plan
        Row currentRow = SSUtilities.copyRow(executionPlansSheet,
                templateForExcecutionStepRow, currentRowIndex);
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
        safeBigDecimalIntoCellWriter(currentRow.getCell(2), step.getCost());
        safeBigDecimalIntoCellWriter(currentRow.getCell(3), step.getCardinality());
        safeBigDecimalIntoCellWriter(currentRow.getCell(4), step.getBytes());
        safeBigDecimalIntoCellWriter(currentRow.getCell(5), step.getCpuCost());
        safeBigDecimalIntoCellWriter(currentRow.getCell(6), step.getIoCost());
        currentRow.getCell(7).setCellValue(step.getAccessPredicates());
        currentRow.getCell(8).setCellValue(step.getFilterPredicates());
        internalCurrentRowIndex++;

        for (ExecutionPlanStep childStep : step.getChildSteps()) {
            internalCurrentRowIndex = writeExecutionPlanStepToExcel(
                    templateForExcecutionStepRow, internalCurrentRowIndex,
                    childStep);
        }

        return internalCurrentRowIndex;
    }

    private void safeBigDecimalIntoCellWriter(Cell cell, BigDecimal value) {
        if (value == null) {
            cell.setCellValue("");
        } else {
            cell.setCellValue(value.doubleValue());
        }
    }

    private String getIndentionAsString(BigDecimal depth) {
        StringBuffer result = new StringBuffer("");

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

            return false;
        }
    }
}
