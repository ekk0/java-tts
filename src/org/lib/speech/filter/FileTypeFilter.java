package org.lib.speech.filter;

import java.io.File;
import java.io.IOException;

import org.lib.speech.exception.UnsupportFileTypeException;
import org.lib.speech.util.FileScanner;
import org.lib.speech.util.PropertiesReader;

public class FileTypeFilter {

	/**
	 * Check if the suffixe of the file is txt.
	 * 
	 * @param file
	 *            A file is given.
	 * @return return FileScanner that suppert to convert from a file to string.
	 * @throws UnsupportFileTypeException
	 */
	public static FileScanner getScanner(File file)
			throws UnsupportFileTypeException {
		String fileName = file.getName();
		int index = -1;
		if ((index = fileName.lastIndexOf(".")) != -1) {
			String subName = fileName.substring(index + 1);
			String className = null;

			try {
				className = PropertiesReader.getValue(
						"properties/fileTypeRegistry.properties", subName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (className != null) {
				try {
					return (FileScanner) Class.forName(className).newInstance();
				} catch (Exception e) {
					throw new UnsupportFileTypeException(
							"Error occurred while create an instance of a FileScanner.You should check the class which support this type of file.");
				}
			} else {
				throw new UnsupportFileTypeException(
						"You haven't defined any class to scan this type of file.");
			}
		} else {
			throw new UnsupportFileTypeException(
					"This file type is not supported currently.");
		}
	}
}
