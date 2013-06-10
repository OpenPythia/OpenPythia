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
package org.openpythia.utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;

public class SSUtilities {

    public static Row copyRow(Sheet sheet, Row sourceRow, int destination) {
        Row newRow = sheet.createRow(destination);
        // get the last row from the headings
        int lastCol = sheet.getRow(0).getLastCellNum();
        for (int currentCol = 0; currentCol <= lastCol; currentCol++) {
            Cell newCell = newRow.createCell(currentCol);

            // if there is a cell in the template, copy its content and style
            Cell currentCell = sourceRow.getCell(currentCol);
            if (currentCell != null) {
                newCell.setCellStyle(currentCell.getCellStyle());
                newCell.setCellComment(currentCell.getCellComment());
                switch (currentCell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        newCell.setCellValue(currentCell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        newCell.setCellValue(currentCell.getNumericCellValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                        String dummy = currentCell.getCellFormula();
                        dummy = dummy.replace("Row",
                                String.valueOf(destination + 1));
                        newCell.setCellFormula(dummy);
                        newCell.setCellFormula(currentCell
                                .getCellFormula()
                                .replace("Row", String.valueOf(destination + 1)));
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        newCell.setCellValue(currentCell.getBooleanCellValue());
                        break;
                    default:
                }
            }
        }

        // if the row contains merged regions, copy them to the new row
        int numberMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numberMergedRegions; i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            if (mergedRegion.getFirstRow() == sourceRow.getRowNum()
                    && mergedRegion.getLastRow() == sourceRow.getRowNum()) {
                // this region is within the row - so copy it
                sheet.addMergedRegion(new CellRangeAddress(destination,
                        destination, mergedRegion.getFirstColumn(), mergedRegion
                        .getLastColumn()));
            }
        }

        return newRow;
    }

    public static void deleteRow(Sheet sheet, Row rowToDelete) {

        // if the row contains merged regions, delete them
        List<Integer> mergedRegionsToDelete = new ArrayList<Integer>();
        int numberMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numberMergedRegions; i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);

            if (mergedRegion.getFirstRow() == rowToDelete.getRowNum()
                    && mergedRegion.getLastRow() == rowToDelete.getRowNum()) {
                // this region is within the row - so mark it for deletion
                mergedRegionsToDelete.add(i);
            }
        }

        // now that we know all regions to delete just do it
        for (Integer indexToDelete : mergedRegionsToDelete) {
            sheet.removeMergedRegion(indexToDelete);
        }

        int rowIndex = rowToDelete.getRowNum();

        // this only removes the content of the row
        sheet.removeRow(rowToDelete);

        int lastRowNum = sheet.getLastRowNum();

        // shift the rest of the sheet one index down
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
        }
    }

}
