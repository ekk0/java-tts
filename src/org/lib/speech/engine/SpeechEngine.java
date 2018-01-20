package org.lib.speech.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lib.speech.analysis.AnalyzeCenter;
import org.lib.speech.exception.SpeechSynthesisException;
import org.lib.speech.exception.UnsupportFileTypeException;
import org.lib.speech.filter.FileTypeFilter;
import org.lib.speech.index.IndexManager;
import org.lib.speech.process.ProcessCenter;
import org.lib.speech.process.SimpleAudioPlayer;

import com.spreada.utils.chinese.ZHConverter;

public class SpeechEngine implements Engine {

	public SpeechEngine(ProcessCenter pc, boolean isOral) {

		this.pc = pc;
		this.isOral = isOral;

		analCen = pc.getAnalCen();
		indexManager = pc.getIndexManager();

		simplePlayer = new SimpleAudioPlayer();
	}

	public SpeechEngine(boolean isOral) {
		this.isOral = isOral;

		simplePlayer = new SimpleAudioPlayer();
	}

	public SpeechEngine(AnalyzeCenter analCen, IndexManager indexManager,
			boolean isOral) {
		this.analCen = analCen;
		this.indexManager = indexManager;
		this.isOral = isOral;

		simplePlayer = new SimpleAudioPlayer();
	}

	/**
	 * Pronounce the words.
	 * 
	 * @param sentences
	 *            The input sentences.
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public void getPronounces(String sentences) {
		try {
			AudioInputStream audioInputStream = this
					.getAudioInputStream(sentences);
			System.out.println("Pronouncing...");
			simplePlayer.audioPlay(audioInputStream);
		} catch (SpeechSynthesisException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			System.out.println("Program exit!");
		}
	}

	/**
	 * Pronounce the words in the file.
	 * 
	 * @param file
	 * @param isPrintDocument
	 * @throws IOException
	 */
	public void getPronounces(File file, boolean isPrintDocument)
			throws IOException {
		try {
			String sentences = FileTypeFilter.getScanner(file).conver(file);
			if (isPrintDocument) {
				System.out.println("Original document below:");
				System.out.println(sentences + "\n");
			}
			this.getPronounces(sentences);
		} catch (UnsupportFileTypeException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Generate an .au file with sentences.
	 * 
	 * @param sentences
	 *            Input sentences.
	 * @param targetFile
	 *            Output audio file currently only support .au file.
	 * @throws IOException
	 */
	public void getPronouncesFile(String sentences, File targetFile)
			throws IOException {
		try {
			AudioSystem.write(this.getAudioInputStream(sentences),
					AudioFileFormat.Type.AU, targetFile);
			System.out.println("AudioFile generate success!");
		} catch (SpeechSynthesisException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			System.out.println("Program exit!");
		}
	}

	public void getPronouncesFile(File sourceDocumentFile, File targetFile)
			throws IOException {
		try {
			String sentences = FileTypeFilter.getScanner(sourceDocumentFile)
					.conver(sourceDocumentFile);
			this.getPronouncesFile(sentences, targetFile);
		} catch (UnsupportFileTypeException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Get AudioInputStream.
	 * 
	 * @param sentences
	 * @return
	 * @throws SpeechSynthesisException
	 */
	private AudioInputStream getAudioInputStream(String sentences)
			throws SpeechSynthesisException {
		return pc.pronounceElementProcess(getPronounceElements(sentences));
	}

	/**
	 * 
	 * @param sentences
	 *            Input sentences.
	 * @return The file list represent the result of Chinese segment. Of course,
	 *         the sound files(.wav) that match the words consider the situation
	 *         in context.
	 */
	public List<Object[]> getPronounceElements(String sentences) {
		List<Object[]> phrasesPronounceFiles = new ArrayList<Object[]>();
		sentences = this.strToJian(sentences); // 繁体转简体
		List<String> codes = analCen.convert(sentences);
		if (codes != null) {
			Iterator<String> iter = codes.iterator();
			while (iter.hasNext()) {
				String praseCode = iter.next();
				Object[] pronounceElement = indexManager
						.getPrasesPronounceElements(praseCode, isOral);
				phrasesPronounceFiles.add(pronounceElement);
			}
		}
		return phrasesPronounceFiles;
	}

	/**
	 * Convert traditional Chinese to simple Chinese.
	 * 
	 * @param sentences
	 * @return
	 */
	public String strToJian(String sentences) {
		return fanToJian.convert(sentences);
	}

	public AnalyzeCenter getAnalCen() {
		return analCen;
	}

	public void setAnalCen(AnalyzeCenter analCen) {
		this.analCen = analCen;
	}

	public IndexManager getIndexManager() {
		return indexManager;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	private AnalyzeCenter analCen;
	private IndexManager indexManager;
	private ProcessCenter pc;
	private boolean isOral; // 是否转换为口语
	private SimpleAudioPlayer simplePlayer;
	private ZHConverter fanToJian = ZHConverter
			.getInstance(ZHConverter.SIMPLIFIED);
}
