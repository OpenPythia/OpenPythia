package org.openpythia.utilities;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileFilterExcel extends FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}

		String extension = FileSelectorUtility.getExtension(file);
		if (extension != null) {
			if (extension.equals("xls")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Excel Files (.xls)";
	}

}
