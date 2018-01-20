package org.lib.speech.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lib.speech.analysis.AnalyzeCenter;
import org.lib.speech.analysis.SentencesConverter;
import org.lib.speech.exception.SpeechSynthesisException;
import org.lib.speech.index.JyutPingDictionary;
import org.lib.speech.index.IndexManager;
import org.lib.speech.index.JyutPingPronounceElementsIndex;

public class DefaultStreamProcess implements ProcessCenter {

	public DefaultStreamProcess(AnalyzeCenter analCen,
			IndexManager indexManager, float pitch, long duration) {
		this.analCen = analCen;
		this.indexManager = indexManager;
		this.pitch = pitch;
		this.duration = duration;
	}

	public DefaultStreamProcess(AnalyzeCenter analCen,
			IndexManager indexManager, SpeechPitch ePitch,
			SpeechDuration eDuration) {
		this.analCen = analCen;
		this.indexManager = indexManager;
		this.ePitch = ePitch;
		this.eDuration = eDuration;
	}

	public DefaultStreamProcess(AnalyzeCenter analCen,
			IndexManager indexManager, float pitch, SpeechDuration eDuration) {
		this.analCen = analCen;
		this.indexManager = indexManager;
		this.pitch = pitch;
		this.eDuration = eDuration;
	}

	public DefaultStreamProcess(AnalyzeCenter analCen,
			IndexManager indexManager, SpeechPitch ePitch, long duration) {
		this.analCen = analCen;
		this.indexManager = indexManager;
		this.ePitch = ePitch;
		this.duration = duration;
	}

	public DefaultStreamProcess(float pitch, long duration, boolean isRefresh) {
		this.analCen = new SentencesConverter();
		this.indexManager = new JyutPingPronounceElementsIndex(
				JyutPingDictionary.getInstance(isRefresh));
	}

	public DefaultStreamProcess(SpeechPitch ePitch, long duration,
			boolean isRefresh) {
		this.analCen = new SentencesConverter();
		this.indexManager = new JyutPingPronounceElementsIndex(
				JyutPingDictionary.getInstance(isRefresh));
		this.ePitch = ePitch;
		this.duration = duration;
	}

	public DefaultStreamProcess(float pitch, SpeechDuration eDuration,
			boolean isRefresh) {
		this.analCen = new SentencesConverter();
		this.indexManager = new JyutPingPronounceElementsIndex(
				JyutPingDictionary.getInstance(isRefresh));
		this.pitch = pitch;
		this.eDuration = eDuration;
	}

	public DefaultStreamProcess(SpeechPitch ePitch, SpeechDuration eDuration,
			boolean isRefresh) {
		this.analCen = new SentencesConverter();
		this.indexManager = new JyutPingPronounceElementsIndex(
				JyutPingDictionary.getInstance(isRefresh));
		this.ePitch = ePitch;
		this.eDuration = eDuration;
	}

	public DefaultStreamProcess(boolean isRefresh) {
		this.analCen = new SentencesConverter();
		this.indexManager = new JyutPingPronounceElementsIndex(
				JyutPingDictionary.getInstance(isRefresh));
	}

	/**
	 * Create every pronounce elements' audioStream.
	 * 
	 * @param pronounceElements
	 *            Pronounce elements.
	 * @return The whole senetens pronounce audioStream.
	 * @throws SpeechSynthesisException
	 */
	public AudioInputStream pronounceElementProcess(
			List<Object[]> pronounceElements) throws SpeechSynthesisException {

		List<AudioInputStream> audioInputStreamList = new ArrayList<AudioInputStream>();
		AudioInputStream audioInputStream = null;

		Iterator<Object[]> iter = pronounceElements.iterator();

		try {
			while (iter.hasNext()) {
				Object[] obj = iter.next();
				if (obj[0] instanceof File) { // 如果一个片段的第一个字符是文字，则这个片段所有的字符都是文字
					int prasesCount = obj.length;
					for (int i = 0; i < prasesCount; i++) {
						File audioFile = (File) obj[i];
						audioInputStream = AudioSystem
								.getAudioInputStream(audioFile);
						if (i == 0 && prasesCount > 1) {
							// 裁剪后部
							audioInputStream = streamEndCut(audioInputStream,
									10);
						} else if (i < prasesCount - 1) {
							// 两边裁剪
							audioInputStream = streamBothCut(audioInputStream,
									5, 5);
						} else if ((i == prasesCount - 1 && prasesCount > 1)
								|| prasesCount == 1) {
							// 裁剪前部
							audioInputStream = streamBeginCut(audioInputStream,
									10);
						}

						if (!checkFormat(audioInputStream.getFormat())) {
							throw new SpeechSynthesisException(
									"AudioInputStream format mismatching Exception");
						} else {
							audioInputStreamList.add(audioInputStream);
						}
					}
				} else if (obj[0] instanceof Integer) {
					// 标点符号停顿, 只有一个值(obj.length = 1)
					Integer value = (Integer) obj[0];
					if (audioFormat != null) {
						audioInputStream = new SilenceAudioInputStream(
								audioFormat,
								ProcessCenter.PUNCTUATION_WAIT_TIME * value);
						audioInputStreamList.add(audioInputStream);
					} else {
						throw new SpeechSynthesisException(
								"Elements Process Exception because audioFormat is null");
					}
				} else if (obj[0] == null && obj.length == 1) {
					// 未知字符(英文, 空格, 未知标点符号等)
					if (audioFormat != null) {
						audioInputStream = new SilenceAudioInputStream(
								audioFormat,
								ProcessCenter.PUNCTUATION_WAIT_TIME);
						audioInputStreamList.add(audioInputStream);
					} else {
						System.err
								.println("Warnning: The first word is English and pronounce library doesn't have .wav file matched the word!");
						AudioFormat format = new AudioFormat(
								AudioFormat.Encoding.PCM_SIGNED, 44100f, 16, 1,
								2, 44100f, false);
						audioInputStream = new SilenceAudioInputStream(format,
								ProcessCenter.PUNCTUATION_WAIT_TIME);
						audioInputStreamList.add(audioInputStream);
						// throw new SpeechSynthesisException(
						// "Elements Process Exception because audioFormat is null");
					}
				} else {
					throw new SpeechSynthesisException(
							"Received abnormal pronounce elements Exception");
				}
			}
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 这里需要处理音调与速率
		return new SequenceAudioInputStream(audioFormat, audioInputStreamList);

	}

	/**
	 * Cut the beginning of the audio input stream.
	 * 
	 * @param audioInputStream
	 * @return
	 * @throws IOException
	 */
	private AudioInputStream streamBeginCut(AudioInputStream audioInputStream,
			int frameLengthCut) throws IOException {
		audioInputStream.skip(frameLengthCut
				* audioInputStream.getFormat().getFrameSize());
		return audioInputStream;
	}

	/**
	 * Cut the ending of the audio input stream.
	 * 
	 * @param audioInputStream
	 * @return
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	private AudioInputStream streamEndCut(AudioInputStream audioInputStream,
			int frameLengthCut) throws IOException,
			UnsupportedAudioFileException {
		byte[] b = new byte[ProcessCenter.EXTERNAL_BUFFER_SIZE];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		long finalFramelength = audioInputStream.getFrameLength()
				- frameLengthCut;
		long totleByte = finalFramelength
				* audioInputStream.getFormat().getFrameSize();

		long currentByte = 0;
		int length = -1;
		while ((length = audioInputStream.read(b, 0, b.length)) != -1) {
			if (currentByte + length <= totleByte) {
				bos.write(b, 0, length);
				currentByte += length;
			} else {
				length = (int) (totleByte - currentByte);
				bos.write(b, 0, length);
				bos.close();
				break;
			}
		}

		return new AudioInputStream(
				new ByteArrayInputStream(bos.toByteArray()), audioInputStream
						.getFormat(), finalFramelength);
	}

	/**
	 * Cut both beginning and ending of the audio input stream.
	 * 
	 * @param audioInputStream
	 * @return
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	private AudioInputStream streamBothCut(AudioInputStream audioInputStream,
			int beginFrameLengthCut, int endFrameLengthCut) throws IOException,
			UnsupportedAudioFileException {
		return streamEndCut(streamBeginCut(audioInputStream,
				beginFrameLengthCut), endFrameLengthCut);
	}

	private boolean checkFormat(AudioFormat format) {
		boolean flag = false;
		if (audioFormat == null) {
			audioFormat = format;
			flag = true;
		} else {
			if (audioFormat.matches(format)) {
				flag = true;
			}
		}
		return flag;
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

	public float getPitch(AudioInputStream stream) {
		if (pitch - 0 < 1e-4) {
			if (ePitch != null) {
				pitch = ePitch.getPitch(stream);
			} else {
				pitch = SpeechPitch.DEFAULT.getPitch(stream);
			}
		}
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public long getDuration(AudioInputStream stream)
			throws NumberFormatException, UnsupportedAudioFileException,
			IOException {
		if (duration == 0) {
			if (eDuration != null) {
				duration = eDuration.getDuration(stream);
			} else {
				duration = SpeechDuration.DEFAULT.getDuration(stream);
			}
		}
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	private AnalyzeCenter analCen;
	private IndexManager indexManager;
	private AudioFormat audioFormat = null;
	private float pitch; // 音调
	private long duration; // duration of playback
	private SpeechPitch ePitch;
	private SpeechDuration eDuration;
}
