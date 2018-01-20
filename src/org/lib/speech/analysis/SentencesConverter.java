package org.lib.speech.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SentencesConverter extends SmartSegment implements AnalyzeCenter {

	public SentencesConverter() {
		super();
	}

	/**
	 * Main function is Chinese words segmentation.
	 * 
	 * @param str
	 *            Input sentence.
	 * @return ASCII represent the segment result.
	 */
	public List<String> convert(String str) {

		List<String> codes = new ArrayList<String>();
		try {
			List<String> phrases = this.doSegment(str);
			if (phrases != null) {
				Iterator<String> iter = phrases.iterator();

				while (iter.hasNext()) {
					char[] term = iter.next().toCharArray();
					String termCode = "";
					for (int i = 0; i < term.length; i++) {
						termCode += (int) term[i] + "|";
					}
					codes.add(termCode);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return codes;
	}
}
