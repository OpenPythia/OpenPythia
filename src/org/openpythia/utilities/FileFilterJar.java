package org.openpythia.utilities;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileFilterJar extends FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}

		String extension = FileSelectorUtility.getExtension(file);
		if (extension != null) {
			if (extension.equals("jar")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Jar Files (.jar)";
	}

}
