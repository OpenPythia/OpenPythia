package org.openpythia.plugin.worststatements;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openpythia.utilities.FileRessourceUtility;
import org.openpythia.utilities.SSUtilities;
import org.openpythia.utilities.deltasql.DeltaSQLStatementSnapshot;
import org.openpythia.utilities.deltasql.DeltaSnapshot;

public class DeltaSnapshotWriter {

    public static void saveDeltaSnapshot(File destination,
            DeltaSnapshot deltaSnapshot) {

        try {
            FileRessourceUtility.copyFile("Template_DELTA_V$SQLAREA.xls",
                    destination);

            Workbook workbook = WorkbookFactory.create(new FileInputStream(
                    destination));

            writeDeltaSnapshotStatements(workbook.getSheet("Delta V$SQLAREA"),
                    deltaSnapshot);

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

    private static void writeDeltaSnapshotStatements(Sheet sheet,
            DeltaSnapshot deltaSnapshot) {
        Row templateRow = sheet.getRow(3);

        int currentRowIndex = 3;

        for (DeltaSQLStatementSnapshot currentSnapshot : deltaSnapshot
                .getDeltaSqlStatementSnapshots()) {
            Row currentRow = SSUtilities.copyRow(sheet, templateRow,
                    currentRowIndex);

            currentRow.getCell(0).setCellValue(
                    currentSnapshot.getSqlStatement().getParsingSchema());
            currentRow.getCell(1).setCellValue(
                    currentSnapshot.getSqlStatement().getSqlText());
            currentRow.getCell(2).setCellValue(
                    currentSnapshot.getDeltaExecutions());
            currentRow.getCell(4).setCellValue(
                    currentSnapshot.getDeltaElapsedSeconds());
            currentRow.getCell(7).setCellValue(
                    currentSnapshot.getDeltaCpuSeconds());
            currentRow.getCell(10).setCellValue(
                    currentSnapshot.getDeltaBufferGets());
            currentRow.getCell(12).setCellValue(
                    currentSnapshot.getDeltaDiskReads());
            currentRow.getCell(14).setCellValue(
                    currentSnapshot.getDeltaRowsProcessed());
            currentRow.getCell(16).setCellValue(
                    currentSnapshot.getSqlStatement().getSqlId());
            currentRow.getCell(17).setCellValue(
                    currentSnapshot.getSqlStatement().getAddress());

            currentRowIndex++;
        }

        // update the formulas in the third row (sum)
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper()
                .createFormulaEvaluator();
        Iterator<Cell> cellIterator = sheet.getRow(2).cellIterator();
        while (cellIterator.hasNext()) {
            Cell currentCell = cellIterator.next();
            if (currentCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                evaluator.evaluateFormulaCell(currentCell);
            }
        }
    }
}
