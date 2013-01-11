package org.openpythia.plugin.worststatements;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
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
import org.openpythia.dbconnection.ConnectionPool;
import org.openpythia.utilities.FileRessourceUtility;
import org.openpythia.utilities.SSUtilities;
import org.openpythia.utilities.deltasql.DeltaSQLStatementSnapshot;
import org.openpythia.utilities.deltasql.DeltaSnapshot;
import org.openpythia.utilities.sql.ExecutionPlan;
import org.openpythia.utilities.sql.ExecutionPlanStep;
import org.openpythia.utilities.sql.SQLHelper;
import org.openpythia.utilities.sql.SQLStatement;

public class DeltaSnapshotWriter {

    private static final int INDEX_ROW_TEMPLATE_DELTA_SQL_STATEMENT = 3;
    private static final int INDEX_ROW_SUM_FORMULAS = 2;

    private static final int INDEX_COLUMN_ADDRESS = 17;
    private static final int INDEX_COLUMN_SQL_ID = 16;
    private static final int INDEX_COLUMN_DELTA_ROWS_PROCESSED = 14;
    private static final int INDEX_COLUMN_DELTA_DISK_READS = 12;
    private static final int INDEX_COLUMN_DELTA_BUFFER_GETS = 10;
    private static final int INDEX_COLUMN_DELTA_CPU_SECONDS = 7;
    private static final int INDEX_COLUMN_DELTA_ELAPSED_SECONDS = 4;
    private static final int INDEX_COLUMN_DELTA_EXECUTIONS = 2;
    private static final int INDEX_COLUMN_SQL_TEXT = 1;
    private static final int INDEX_COLUMN_PARSING_SCHEMA = 0;

    private ConnectionPool connectionPool;
    private File destination;
    private DeltaSnapshot deltaSnapshot;

    private Sheet statementsSheet;
    private Sheet executionPlansSheet;
    private CellStyle hyperlinkStyle;

    private DeltaSnapshotWriter(ConnectionPool connectionPool,
            File destination, DeltaSnapshot deltaSnapshot) {

        this.connectionPool = connectionPool;
        this.destination = destination;
        this.deltaSnapshot = deltaSnapshot;
    }

    private void saveDeltaSnapshot() {
        try {
            FileRessourceUtility.copyFile("Template_DELTA_V$SQLAREA.xls",
                    destination);

            Workbook workbook = WorkbookFactory.create(new FileInputStream(
                    destination));
            statementsSheet = workbook.getSheet("Delta V$SQLAREA");
            executionPlansSheet = workbook.getSheet("Execution Plans");
            hyperlinkStyle = createHyperlinkStyle(workbook);

            writeDeltaSnapshotStatements();

            writeExecutionPlansForWorstStatements();

            OutputStream output = new FileOutputStream(destination);
            workbook.write(output);
            output.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        } catch (InvalidFormatException e) {
            JOptionPane.showMessageDialog((Component) null, e);
        }
    }

    public static void saveDeltaSnapshot(ConnectionPool connectionPool,
            File destination, DeltaSnapshot deltaSnapshot) {

        DeltaSnapshotWriter writer = new DeltaSnapshotWriter(connectionPool,
                destination, deltaSnapshot);

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

        int currentRowIndex = 3;

        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot
                .getDeltaSqlStatementSnapshots()) {
            Row currentRow = SSUtilities.copyRow(statementsSheet, templateRow,
                    currentRowIndex);

            currentRow.getCell(INDEX_COLUMN_PARSING_SCHEMA).setCellValue(
                    currentSnapshot.getSqlStatement().getParsingSchema());
            currentRow.getCell(INDEX_COLUMN_SQL_TEXT).setCellValue(
                    currentSnapshot.getSqlStatement().getSqlText());
            currentRow.getCell(INDEX_COLUMN_DELTA_EXECUTIONS).setCellValue(
                    currentSnapshot.getDeltaExecutions());
            currentRow.getCell(INDEX_COLUMN_DELTA_ELAPSED_SECONDS)
                    .setCellValue(currentSnapshot.getDeltaElapsedSeconds());
            currentRow.getCell(INDEX_COLUMN_DELTA_CPU_SECONDS).setCellValue(
                    currentSnapshot.getDeltaCpuSeconds());
            currentRow.getCell(INDEX_COLUMN_DELTA_BUFFER_GETS).setCellValue(
                    currentSnapshot.getDeltaBufferGets());
            currentRow.getCell(INDEX_COLUMN_DELTA_DISK_READS).setCellValue(
                    currentSnapshot.getDeltaDiskReads());
            currentRow.getCell(INDEX_COLUMN_DELTA_ROWS_PROCESSED).setCellValue(
                    currentSnapshot.getDeltaRowsProcessed());
            currentRow.getCell(INDEX_COLUMN_SQL_ID).setCellValue(
                    currentSnapshot.getSqlStatement().getSqlId());
            currentRow.getCell(INDEX_COLUMN_ADDRESS).setCellValue(
                    currentSnapshot.getSqlStatement().getAddress());

            currentRowIndex++;
        }

        // update the formulas in the third row (sum)
        FormulaEvaluator evaluator = statementsSheet.getWorkbook()
                .getCreationHelper().createFormulaEvaluator();
        Iterator<Cell> cellIterator = statementsSheet.getRow(INDEX_ROW_SUM_FORMULAS).cellIterator();
        while (cellIterator.hasNext()) {
            Cell currentCell = cellIterator.next();
            if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                evaluator.evaluateFormulaCell(currentCell);
            }
        }
    }

    private void writeExecutionPlansForWorstStatements() {

        Row templateStatementHeaderRow = executionPlansSheet.getRow(3);
        Row templateChildHeaderRow = executionPlansSheet.getRow(4);
        Row templateExecutionStepRow = executionPlansSheet.getRow(5);

        executionPlansSheet.removeRow(templateExecutionStepRow);
        executionPlansSheet.removeRow(templateChildHeaderRow);
        executionPlansSheet.removeRow(templateStatementHeaderRow);

        WorstStatementIdentifier wsi = new WorstStatementIdentifier(
                deltaSnapshot);

        // Identify the worst statements and load their execution plans
        List<SQLStatement> worstStatements = new ArrayList<SQLStatement>();
        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot
                .getDeltaSqlStatementSnapshots()) {

            if (wsi.isAWorstStatement(currentSnapshot)) {
                worstStatements.add(currentSnapshot.getSqlStatement());
            }
        }
        SQLHelper.loadExecutionPlansForStatements(connectionPool,
                worstStatements, null);

        // Now write the execution plans into the Excel sheet
        int currentRowIndex = 3;
        Row currentRow;

        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot
                .getDeltaSqlStatementSnapshots()) {

            if (wsi.isAWorstStatement(currentSnapshot)) {
                // Header for statement
                currentRow = SSUtilities.copyRow(executionPlansSheet,
                        templateStatementHeaderRow, currentRowIndex);
                currentRow.getCell(0).setCellValue(
                        currentSnapshot.getSqlStatement().getSqlId());
                currentRow.getCell(1).setCellValue(
                        currentSnapshot.getSqlStatement().getAddress());
                currentRow.getCell(2).setCellValue(
                        currentSnapshot.getSqlStatement().getSqlText());
                currentRowIndex++;

                // Link from sheet with statements to this execution plan
                Hyperlink link = statementsSheet.getWorkbook()
                        .getCreationHelper()
                        .createHyperlink(Hyperlink.LINK_DOCUMENT);
                link.setAddress("'" + executionPlansSheet.getSheetName()
                        + "'!A" + currentRowIndex);
                addLinkFromStatementToExecutionPlans(currentSnapshot
                        .getSqlStatement().getAddress(), link);

                for (ExecutionPlan currentPlan : currentSnapshot
                        .getSqlStatement().getExecutionPlans()) {
                    // Header for Child / Execution Plan
                    // There may be more than one execution plans...
                    currentRow = SSUtilities.copyRow(executionPlansSheet,
                            templateChildHeaderRow, currentRowIndex);
                    currentRow.getCell(0).setCellValue(
                            currentPlan.getChildNumber());
                    currentRowIndex++;

                    currentRowIndex = writeExecutionPlanStepToExcel(
                            templateExecutionStepRow, currentRowIndex,
                            currentPlan.getParentStep());
                }
            }
        }
    }

    private void addLinkFromStatementToExecutionPlans(String address,
            Hyperlink hyperlinkToExecutionPlanCell) {

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
        currentRow.getCell(2).setCellValue(step.getCost());
        currentRow.getCell(3).setCellValue(step.getCardinality());
        currentRow.getCell(4).setCellValue(step.getBytes());
        currentRow.getCell(5).setCellValue(step.getCpuCost());
        currentRow.getCell(6).setCellValue(step.getIoCost());
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

    private String getIndentionAsString(int depth) {
        StringBuffer result = new StringBuffer("");

        for (int i = 1; i <= depth; i++) {
            result.append("  ");
        }

        return result.toString();
    }

    // Is a statement a "worst statement"?
    // To find out, we need to look at this statement compared to all the
    // statements: A statement gets a worst statement, when it eats up a
    // big amount of runtime, CPU, buffer gets...
    private static class WorstStatementIdentifier {

        private BigInteger sumExecutions = BigInteger.ZERO;
        private BigInteger sumElaspsedSeconds = BigInteger.ZERO;
        private BigInteger sumCPUSeconds = BigInteger.ZERO;
        private BigInteger sumBufferGets = BigInteger.ZERO;
        private BigInteger sumDiskReads = BigInteger.ZERO;

        public WorstStatementIdentifier(DeltaSnapshot deltaSnapshot) {
            // Calculate and store some sums to compare against single
            // statements
            for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot
                    .getDeltaSqlStatementSnapshots()) {

                sumExecutions = sumExecutions.add(BigInteger
                        .valueOf(currentSnapshot.getDeltaExecutions()));
                sumElaspsedSeconds = sumElaspsedSeconds.add(BigInteger
                        .valueOf(currentSnapshot.getDeltaElapsedSeconds()));
                sumCPUSeconds = sumCPUSeconds.add(BigInteger
                        .valueOf(currentSnapshot.getDeltaCpuSeconds()));
                sumBufferGets = sumBufferGets.add(BigInteger
                        .valueOf(currentSnapshot.getDeltaBufferGets()));
                sumDiskReads = sumDiskReads.add(BigInteger
                        .valueOf(currentSnapshot.getDeltaDiskReads()));
            }
        }

        public boolean isAWorstStatement(DeltaSQLStatementSnapshot snapshot) {
            // Implementation is analog to the conditional formatting in the
            // Excel sheet

            // more than 1 % of the total
            if (BigInteger.valueOf(snapshot.getDeltaExecutions() * 100)
                    .compareTo(sumExecutions) > 0) {
                return true;
            }
            if (BigInteger.valueOf(snapshot.getDeltaElapsedSeconds() * 100)
                    .compareTo(sumElaspsedSeconds) > 0) {
                return true;
            }
            if (BigInteger.valueOf(snapshot.getDeltaCpuSeconds() * 100)
                    .compareTo(sumCPUSeconds) > 0) {
                return true;
            }
            if (BigInteger.valueOf(snapshot.getDeltaBufferGets() * 100)
                    .compareTo(sumBufferGets) > 0) {
                return true;
            }
            if (BigInteger.valueOf(snapshot.getDeltaDiskReads() * 100)
                    .compareTo(sumDiskReads) > 0) {
                return true;
            }

            return false;
        }
    }
}
