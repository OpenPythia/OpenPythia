package org.openpythia.utilities;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class FileSelectorUtility {

    private static File lastPath;

    private FileSelectorUtility() {
    }

    /**
     * Get the extension of a file.
     * 
     * @param file
     *            File for which the extension should be returned.
     * @return The extension of the file, null if there is no.
     */
    public static String getExtension(File file) {
        String ext = null;
        String fileName = file.getName();
        int indexExtension = fileName.lastIndexOf('.');

        if (indexExtension > 0 && indexExtension < fileName.length() - 1) {
            ext = fileName.substring(indexExtension + 1).toLowerCase();
        }
        return ext;
    }

    public static File chooseExcelFileToWrite(Component owner) {
        return chooseFileToWrite(owner, new FileFilterExcel(), ".xls", null);
    }

    public static File chooseExcelFileToWrite(Component owner, String fileName) {
        return chooseFileToWrite(owner, new FileFilterExcel(), ".xls", fileName);
    }

    public static File chooseSQLFileToWrite(Component owner) {
        return chooseFileToWrite(owner, new FileFilterSQL(), ".sql", null);
    }

    public static File chooseSQLFileToWrite(Component owner, String fileName) {
        return chooseFileToWrite(owner, new FileFilterSQL(), ".sql", fileName);
    }

    private static File chooseFileToWrite(Component owner, FileFilter filter,
            String extension, String fileName) {

        File result = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(filter);
        if (lastPath != null) {
            fileChooser.setCurrentDirectory(lastPath);
        }
        if (fileName != null) {
            fileChooser.setSelectedFile(new File(fileName));
        }

        int returnVal = fileChooser.showSaveDialog(owner);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            result = fileChooser.getSelectedFile();
            lastPath = result.getParentFile();

            if (FileSelectorUtility.getExtension(result) == null) {
                result = new File(result.getAbsolutePath() + extension);
            }

            if (result != null && result.exists()) {
                int answer = JOptionPane.showConfirmDialog(owner,
                        "This file already exists. Overwrite?", "File exists",
                        JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.NO_OPTION) {
                    result = null;
                }
            }
        }
        return result;
    }

    public static File chooseExcelFileToRead(Component owner) {
        return chooseFileToRead(owner, new FileFilterExcel());
    }

    public static File chooseJarFileToRead() {
        return chooseFileToRead(null, new FileFilterJar());
    }

    private static File chooseFileToRead(Component owner, FileFilter filter) {

        File result = null;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(filter);
        if (lastPath != null) {
            fileChooser.setCurrentDirectory(lastPath);
        }

        int returnVal = fileChooser.showOpenDialog(owner);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            result = fileChooser.getSelectedFile();
            lastPath = result.getParentFile();
        }
        return result;
    }
}
