package org.lib.speech.util;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;

import java.io.File;
import java.nio.charset.Charset;

public class CharsetDetector {
	@SuppressWarnings("deprecation")
	public static Charset getCharset(File file) {
		Charset charset = null;
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();

		detector.add(new ByteOrderMarkDetector());
		detector.add(JChardetFacade.getInstance());
		detector.add(ASCIIDetector.getInstance());

		try {
			charset = detector.detectCodepage(file.toURL());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (charset != null) {
			return charset;
		} else {
			return Charset.defaultCharset();
		}
	}
}
