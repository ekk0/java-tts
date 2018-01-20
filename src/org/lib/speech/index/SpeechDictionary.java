package org.lib.speech.index;

import java.io.Serializable;
import java.util.HashMap;

public interface SpeechDictionary extends Serializable {

	public void readTxtDict();

	public void readStopWordsDict();

	public void readOralDict();

	public HashMap<String, String> getTxtCodeMap();

	public HashMap<String, Integer> getSwCodeMap();

	public HashMap<String, String> getOralMap();
}
