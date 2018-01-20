package org.lib.speech.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class TxtFileToString implements FileScanner {

	public String conver(File file) throws IOException {
		synchronized (classLock) {

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), this.getCharset(file)));
			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			return sb.toString();
		}
	}

	private Charset getCharset(File file) {
		return CharsetDetector.getCharset(file);
	}

	private static Object classLock = TxtFileToString.class;
}
