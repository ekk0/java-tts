package org.lib.speech.analysis;

import java.io.IOException;
import java.util.List;

public interface AnalyzeCenter {
	public List<String> doSegment(String str) throws IOException;

	public List<String> convert(String str);
}
