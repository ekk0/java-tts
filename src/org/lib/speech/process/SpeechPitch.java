package org.lib.speech.process;

import javax.sound.sampled.AudioInputStream;

public enum SpeechPitch {
	DEFAULT, MALE, FEMALE;

	public float getPitch(AudioInputStream stream) {
		float defaultPitch = 0;
		switch (this) {
		case MALE:
			return defaultPitch * 0.7f;
		case FEMALE:
			return defaultPitch * 1.3f;
		default:
			return defaultPitch;

		}
	}
}
