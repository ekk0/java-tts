package org.lib.speech.process;

import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lib.speech.analysis.AnalyzeCenter;
import org.lib.speech.exception.SpeechSynthesisException;
import org.lib.speech.index.IndexManager;

public interface ProcessCenter {

	public static final long PUNCTUATION_WAIT_TIME = 400;
	public static final int EXTERNAL_BUFFER_SIZE = 128000;

	public AudioInputStream pronounceElementProcess(List<Object[]> list)
			throws SpeechSynthesisException;

	public AnalyzeCenter getAnalCen();

	public void setAnalCen(AnalyzeCenter analCen);

	public IndexManager getIndexManager();

	public void setIndexManager(IndexManager indexManager);

	public float getPitch(AudioInputStream stream);

	public void setPitch(float pitch);

	public long getDuration(AudioInputStream stream)
			throws NumberFormatException, UnsupportedAudioFileException,
			IOException;

	public void setDuration(long duration);
}
