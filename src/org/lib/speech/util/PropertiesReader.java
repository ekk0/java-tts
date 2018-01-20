package org.lib.speech.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

	/**
	 * Read the property file which mark the location of dictionary and
	 * pronounce file.If you change the location or filename of dictionary or
	 * pronounce file, you should modify the value in config.proerties.
	 * 
	 * @param filePath
	 * @param property
	 *            Property name
	 * @return File path which match the property name
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static String getValue(String filePath, String property)
			throws ClassNotFoundException, IOException {
		Properties prop = new Properties();
		InputStream inStream = new FileInputStream(getCanonicalPath()
				+ File.separator + filePath);
		prop.load(inStream);
		return (String) prop.get(property);
	}

	public static String getCanonicalPath() throws IOException {
		return PropertiesReader.file.getCanonicalPath();
	}

	private static File file = new File("..");
}
