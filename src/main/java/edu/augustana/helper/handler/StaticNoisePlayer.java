package edu.augustana.helper.handler;


import edu.augustana.data.HamRadio;

import javax.sound.sampled.*;
import java.util.Random;

public class StaticNoisePlayer {

    private static SourceDataLine line;
    private static Thread noiseThread;
    private static volatile boolean playing;
    private static float volume =(HamRadio.theRadio.getVolume() / 100.0f)  / 5.0f; // Volume factor (0.0 to 1.0)

    public static void setVolume(float newVolume) {
        volume = Math.max(0.0f, Math.min(1.0f, newVolume)); // Clamp between 0.0 and 1.0
    }

    public static void startNoise() throws LineUnavailableException {
        AudioFormat format = new AudioFormat(8000f, 8, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Audio line not supported");
            return;
        }

        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        playing = true;
        noiseThread = new Thread(() -> {
            Random random = new Random();
            byte[] buffer = new byte[1024];
            while (playing) {
                random.nextBytes(buffer);
                // Apply volume adjustment
                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] = (byte) (buffer[i] * volume);
                }
                line.write(buffer, 0, buffer.length);
            }
            line.drain();
            line.close();
        });
        noiseThread.start();
    }

    public static void stopNoise() {
        playing = false;
        if (noiseThread != null) {
            try {
                noiseThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}