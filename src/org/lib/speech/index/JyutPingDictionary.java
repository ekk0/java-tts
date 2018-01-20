package org.lib.speech.index;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import org.lib.speech.util.PropertiesReader;

public class JyutPingDictionary implements SpeechDictionary {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6955767729724275882L;

	private JyutPingDictionary() {
		readTxtDict();
		readStopWordsDict();
		readOralDict();
	}

	/**
	 * Obtain the instance of a Dictionary.
	 * 
	 * @param isRefresh
	 *            If it need to read the dictionary file again.
	 * @return The instance of a Dictionary.
	 */
	public static SpeechDictionary getInstance(boolean isRefresh) {
		synchronized (classLock) {

			try {
				if (!isRefresh) {
					try {
						System.out
								.println("Trying to read serialized dictionary...");
						dict = readDictionary();
						//System.out.println("Done!");
					} catch (FileNotFoundException e) {
						System.out.println("Serialized dictionary not found!");
						JyutPingDictionary.getInstance(true);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					// 重新读取字典文件
					//System.out.println("Trying to read dictionary again...");
					dict = new JyutPingDictionary();
					// 保存序列化文件
					try {
						saveDictionary(dict);
						//System.out.println("Done!");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return dict;
		}
	}

	private static void saveDictionary(SpeechDictionary dict)
			throws FileNotFoundException, IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				PropertiesReader.getCanonicalPath() + File.separator + "data"
						+ File.separator + "jyutpingDict.mem"));
		oos.writeObject(dict);
		oos.close();
		//System.out.println("Save dictionary successful at " + new Date());
	}

	private static SpeechDictionary readDictionary()
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				PropertiesReader.getCanonicalPath() + File.separator + "data"
						+ File.separator + "jyutpingDict.mem"));
		SpeechDictionary dict = (SpeechDictionary) ois.readObject();
		ois.close();
		System.out.println("Read dictionary successful at " + new Date());
		return dict;
	}

	/**
	 * Get Scanner to read the pronounce dictionary.
	 * 
	 * @param dictPath
	 *            The path of pronounce dictionary
	 * @return Scanner of pronounce dictionary file.
	 * @throws IOException
	 */
	private Scanner getScanner(String dictPath) throws IOException {
		File file = new File(PropertiesReader.getCanonicalPath() + dictPath);
		return new Scanner(file, "gbk");
	}

	/**
	 * Skip over the annotation statements.
	 * 
	 * @return If there is not any annotation, then return the current string;
	 *         Or else return the first string behind these annotation. Be
	 *         careful the result may be null.
	 */
	private String next(Scanner scan) {
		String str = null;
		int index = -1;
		while (scan.hasNext()) {
			if ((index = (str = scan.next()).indexOf("//")) != -1) {
				scan.nextLine();
				str = str.substring(0, index);
				if (!str.equals("")) {
					return str;
				}
			} else {
				return str;
			}
		}
		return null;
	}

	/**
	 * Read words dictionary.
	 */
	public void readTxtDict() {
		synchronized (classLock) {
			txtCodeMap = new HashMap<String, String>();
			String code = "", pron = "", temp = "";
			int phrase = 0;
			try {
				Scanner dictScanner = this.getScanner(PropertiesReader
						.getValue("properties/config.properties",
								"index.txtDict.dir"));
				while ((temp = next(dictScanner)) != null) {
					code = temp + "|";
					pron = dictScanner.next();
					txtCodeMap.put(code, pron);
					if ((phrase = Integer.parseInt(dictScanner.next())) != 0) { // 加载词组
						for (int i = 0; i < phrase; i++) {
							code = "";
							pron = "";
							int words = Integer.parseInt(dictScanner.next());
							for (int j = 0; j < words; j++) {
								code += dictScanner.next() + "|";
								pron += dictScanner.next() + "|"; // 以'|'划分读音
							}
							txtCodeMap.put(code, pron);
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Read punctuation dictionary.
	 */
	public void readStopWordsDict() {
		synchronized (classLock) {
			swCodeMap = new HashMap<String, Integer>();
			String code = null, count = "";
			Integer value = -1; // value 对应标点的停顿时长权值
			try {
				Scanner dictScanner = this.getScanner(PropertiesReader
						.getValue("properties/config.properties",
								"index.swDict.dir"));
				while ((count = next(dictScanner)) != null) {
					code = "";
					int length = Integer.parseInt(count);
					for (int i = 0; i < length; i++) {
						code += dictScanner.next() + "|";
					}
					value = Integer.parseInt(dictScanner.next());
					swCodeMap.put(code, value);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Read oral language dictionary.
	 */
	public void readOralDict() {
		synchronized (classLock) {
			oralMap = new HashMap<String, String>();
			String count = null, fWord = null, jWord = null;
			int length = -1;
			try {
				Scanner dictScanner = this.getScanner(PropertiesReader
						.getValue("properties/config.properties",
								"index.oralDict.dir"));
				while ((count = next(dictScanner)) != null) {
					length = Integer.parseInt(count);
					fWord = "";
					for (int i = 0; i < length; i++) {
						fWord += dictScanner.next() + "|";
					}
					length = Integer.parseInt(dictScanner.next());
					jWord = "";
					for (int i = 0; i < length; i++) {
						jWord += dictScanner.next() + "|";
					}
					oralMap.put(fWord, jWord);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * @return Code to pronounce map.
	 */
	public HashMap<String, String> getTxtCodeMap() {
		return txtCodeMap;
	}

	/**
	 * @return Value to punctuation map.
	 */
	public HashMap<String, Integer> getSwCodeMap() {
		return swCodeMap;
	}

	/**
	 * @return
	 */
	public HashMap<String, String> getOralMap() {
		return oralMap;
	}

	private static SpeechDictionary dict;
	private static Object classLock = JyutPingDictionary.class;

	private HashMap<String, String> txtCodeMap;
	private HashMap<String, Integer> swCodeMap;
	private HashMap<String, String> oralMap;
}
