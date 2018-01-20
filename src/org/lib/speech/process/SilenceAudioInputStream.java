package org.lib.speech.process;

/*
 *	SilenceAudioInputStream.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 2003 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 |<---            this code is formatted to fit into 80 columns             --->|
 */

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author Matthias Pfisterer
 */
public class SilenceAudioInputStream extends AudioInputStream {
	// private static final boolean DEBUG = false;

	public SilenceAudioInputStream(AudioFormat audioFormat,
			long lLengthInMilliseconds) {
		super(new SilenceInputStream(audioFormat), audioFormat,
				calculateFrameLengthFromDuration(lLengthInMilliseconds,
						audioFormat.getFrameRate()));
	}

	private static long calculateFrameLengthFromDuration(
			long lLengthInMilliseconds, float fFrameRate) {
		return (long) (lLengthInMilliseconds * fFrameRate / 1000);
	}

	private static class SilenceInputStream extends InputStream {
		private byte[] m_abOneFrameBuffer;

		public SilenceInputStream(AudioFormat audioFormat) {
			m_abOneFrameBuffer = new byte[audioFormat.getFrameSize()];
			if (audioFormat.getEncoding().equals(
					AudioFormat.Encoding.PCM_SIGNED)) {
				/*
				 * We don't need to do anything here, since the elements of
				 * m_abOneFrameBuffer are already initialized to zero.
				 */
			} else if (audioFormat.getEncoding().equals(
					AudioFormat.Encoding.PCM_UNSIGNED)) {
				int nSampleSizeInBits = audioFormat.getSampleSizeInBits();
				int nSampleSizeInBytes = audioFormat.getFrameSize()
						/ audioFormat.getChannels();
				switch (nSampleSizeInBits) {
				case 8:
					m_abOneFrameBuffer[0] = (byte) 0x80;
					break;

				case 16:
					if (audioFormat.isBigEndian()) {
						m_abOneFrameBuffer[0] = (byte) 0x80;
						m_abOneFrameBuffer[1] = (byte) 0x00;
					} else {
						m_abOneFrameBuffer[0] = (byte) 0x00;
						m_abOneFrameBuffer[1] = (byte) 0x80;
					}
					break;

				case 24:
					if (audioFormat.isBigEndian()) {
						m_abOneFrameBuffer[0] = (byte) 0x80;
						m_abOneFrameBuffer[1] = (byte) 0x00;
						m_abOneFrameBuffer[2] = (byte) 0x00;
					} else {
						m_abOneFrameBuffer[0] = (byte) 0x00;
						m_abOneFrameBuffer[1] = (byte) 0x00;
						m_abOneFrameBuffer[2] = (byte) 0x80;
					}
					break;

				case 32:
					if (audioFormat.isBigEndian()) {
						m_abOneFrameBuffer[0] = (byte) 0x80;
						m_abOneFrameBuffer[1] = (byte) 0x00;
						m_abOneFrameBuffer[2] = (byte) 0x00;
						m_abOneFrameBuffer[3] = (byte) 0x00;
					} else {
						m_abOneFrameBuffer[0] = (byte) 0x00;
						m_abOneFrameBuffer[1] = (byte) 0x00;
						m_abOneFrameBuffer[2] = (byte) 0x00;
						m_abOneFrameBuffer[3] = (byte) 0x80;
					}
					break;

				default:
					throw new IllegalArgumentException(
							"sample size not supported");
				}

				/*
				 * Inside the switch statement above, the values have been set
				 * for the first channel. If there is more than one, we have to
				 * duplicate the values for the other channels.
				 */
				for (int i = 1; i < audioFormat.getChannels(); i++) {
					System.arraycopy(m_abOneFrameBuffer, 0, m_abOneFrameBuffer,
							i * nSampleSizeInBytes, nSampleSizeInBytes);
				}
			} else {
				throw new IllegalArgumentException(
						"encoding is not PCM_SIGNED or PCM_UNSIGNED");
			}
		}

		public int read() throws IOException {
			/*
			 * Here, we assum that this method is only called if the frame size
			 * is one. AudioInputStream should guarantee this.
			 */
			return m_abOneFrameBuffer[0];
		}

		public int read(byte[] abBuffer, int nOffset, int nLength) {
			/*
			 * Here, we assume that nLength is a multiple of the frame size.
			 * AudioInputStream should guarantee this.
			 */
			int nFrameSize = m_abOneFrameBuffer.length;
			for (int nBufferPosition = 0; nBufferPosition < nLength; nBufferPosition += nFrameSize) {
				System.arraycopy(m_abOneFrameBuffer, 0, abBuffer, nOffset
						+ nBufferPosition, nFrameSize);
			}
			return nLength;
		}

		/*
		 * We return the maximum of complete frames that fit into an int.
		 */
		public int available() {
			int nFrameSize = m_abOneFrameBuffer.length;
			return (Integer.MAX_VALUE / nFrameSize) * nFrameSize;
		}
	}

	private static void out(String strMessage) {
		System.out.println(strMessage);
	}
}

/*** SilenceAudioInputStream.java ***/
