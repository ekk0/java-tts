package org.lib.speech.index;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.lib.speech.util.PropertiesReader;

public class JyutPingPronounceElementsIndex implements IndexManager {

	public JyutPingPronounceElementsIndex(SpeechDictionary dict) {
		txtCodeMap = dict.getTxtCodeMap();
		swCodeMap = dict.getSwCodeMap();
		oralMap = dict.getOralMap();

		pronounceFilePath = this.getPronounceFilePath();
	}

	/**
	 * Obtain the pronounce directory file path.
	 * 
	 * @return Directory file path
	 */
	public String getPronounceFilePath() {
		String pronounceFilePath = null;
		try {
			pronounceFilePath = PropertiesReader.getValue(
					"properties/config.properties", "index.pron.dir");
			pronounceFilePath = PropertiesReader.getCanonicalPath()
					+ pronounceFilePath;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pronounceFilePath;
	}

	/**
	 * Get pronounce elemnet objects which match the phraseCodes.
	 * 
	 * @param phraseCode
	 *            A String represent unicode of words. Each word must separate
	 *            by '|'.
	 * @return A list of pronounce element objects.
	 */
	public Object[] getPrasesPronounceElements(String phraseCode, boolean isOral) {
		synchronized (classLock) {
			// 转换成口语
			if (isOral) {
				String temp = null;
				if ((temp = oralMap.get(phraseCode)) != null) {
					phraseCode = temp;
				}
			}

			Object[] pronounceElements = null;
			String pronounceStrLine = txtCodeMap.get(phraseCode);
			if (pronounceStrLine != null) { // 一个词组或单字
				String[] pronounceStr = pronounceStrLine.split("[|]");
				pronounceElements = new Object[pronounceStr.length];
				for (int i = 0; i < pronounceStr.length; i++) {
					pronounceElements[i] = new File(pronounceFilePath
							+ File.separator + pronounceStr[i] + ".wav");
				}
			} else { // 发音字典查询词组不存在的时候只能单字符处理
				String[] wordCode = phraseCode.split("[|]");
				String pronStr = txtCodeMap.get(wordCode[0] + "|");
				if (pronStr != null) {
					pronounceElements = new Object[wordCode.length];
					int i = 0;
					while (true) {
						if (pronStr != null) {
							pronounceElements[i] = new File(pronounceFilePath
									+ File.separator + pronStr + ".wav");
						}
						i++;
						if (i < wordCode.length) {
							pronStr = txtCodeMap.get(wordCode[i] + "|");
						} else {
							break;
						}
					}
					;
				} else { // 标点符号或英文等未知字符
					pronounceElements = new Object[1];
					Integer value = swCodeMap.get(phraseCode);
					if (value != null) { // 如果是标点符号，则解析
						pronounceElements[0] = value;
					} else {
						pronounceElements[0] = null; // 未知字符赋值为null
					}
				}
			}
			return pronounceElements;
		}

	}

	private static Object classLock = JyutPingPronounceElementsIndex.class;

	private HashMap<String, String> txtCodeMap;
	private HashMap<String, Integer> swCodeMap;
	private HashMap<String, String> oralMap;
	private String pronounceFilePath;
}
