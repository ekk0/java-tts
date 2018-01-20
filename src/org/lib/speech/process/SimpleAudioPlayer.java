package org.lib.speech.process;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class SimpleAudioPlayer {
	
	public void audioPlay(AudioInputStream audioInputStream) {

		/*
		 * From the AudioInputStream, i.e. from the sound file, we fetch
		 * information about the format of the audio data. These information
		 * include the sampling frequency, the number of channels and the size
		 * of the samples. These information are needed to ask Java Sound for a
		 * suitable output line for this audio file.
		 */
		AudioFormat audioFormat = audioInputStream.getFormat();

		/*
		 * Asking for a line is a rather tricky thing. We have to construct an
		 * Info object that specifies the desired properties for the line.
		 * First, we have to say which kind of line we want. The possibilities
		 * are: SourceDataLine (for playback), Clip (for repeated playback) and
		 * TargetDataLine (for recording). Here, we want to do normal playback,
		 * so we ask for a SourceDataLine. Then, we have to pass an AudioFormat
		 * object, so that the Line knows which format the data passed to it
		 * will have. Furthermore, we can give Java Sound a hint about how big
		 * the internal buffer for the line should be. This isn't used here,
		 * signaling that we don't care about the exact size. Java Sound will
		 * use some default value for the buffer size.
		 */
		SourceDataLine line = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);

			/*
			 * The line is there, but it is not yet ready to receive audio data.
			 * We have to open the line.
			 */
			line.open(audioFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Still not enough. The line now can receive data, but will not pass
		 * them on to the audio output device (which means to your sound card).
		 * This has to be activated.
		 */
		line.start();

		/*
		 * Ok, finally the line is prepared. Now comes the real job: we have to
		 * write data to the line. We do this in a loop. First, we read data
		 * from the AudioInputStream to a buffer. Then, we write from this
		 * buffer to the Line. This is done until the end of the file is
		 * reached, which is detected by a return value of -1 from the read
		 * method of the AudioInputStream.
		 */
		int nBytesRead = 0;
		byte[] abData = new byte[ProcessCenter.EXTERNAL_BUFFER_SIZE];
		while (nBytesRead != -1) {
			try {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				line.write(abData, 0, nBytesRead);
			}
		}

		/*
		 * Wait until all data are played. This is only necessary because of the
		 * bug noted below. (If we do not wait, we would interrupt the playback
		 * by prematurely closing the line and exiting the VM.)
		 * 
		 * Thanks to Margie Fitch for bringing me on the right path to this
		 * solution.
		 */
		line.drain();

		/*
		 * All data are played. We can close the shop.
		 */
		line.close();
	}
}