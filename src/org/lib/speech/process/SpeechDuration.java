package org.lib.speech.process;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public enum SpeechDuration {
	DEFAULT, FAST, SLOW;

	public long getDuration(AudioInputStream stream)
			throws NumberFormatException, UnsupportedAudioFileException,
			IOException {
		long defaultTime = Long.parseLong((String) AudioSystem
				.getAudioFileFormat(stream).getProperty("duration"));
		switch (this) {
		case FAST:
			return (long) (defaultTime * 0.7);
		case SLOW:
			return (long) (defaultTime * 1.3);
		default:
			return defaultTime;
		}
	}
}
