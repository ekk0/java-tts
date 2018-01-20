package org.lib.speech.index;

public interface IndexManager {
	public String getPronounceFilePath();

	public Object[] getPrasesPronounceElements(String phraseCode, boolean isOral);
}
