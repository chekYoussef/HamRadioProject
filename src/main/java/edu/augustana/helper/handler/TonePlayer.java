
package edu.augustana.helper.handler;

import edu.augustana.data.HamRadio;
import edu.augustana.data.Note;

import javax.sound.sampled.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

public class TonePlayer {

    private SourceDataLine line;
    private final AudioFormat audioFormat;
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private Instant toneStartTime;
    private final long minPlayTimeMillis;  // Configurable minimum play time

    // Constructor to set up the audio format and minimum play time
    public TonePlayer(long minPlayTimeMillis) {
        this.audioFormat = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
        this.minPlayTimeMillis = minPlayTimeMillis;  // Set the minimum play time (in milliseconds)
    }

    // Start playing audio
    public void startAudio()  {
        if (!isPlaying.getAndSet(true)) {
            try {
                if (line == null || !line.isOpen()) {
                    line = AudioSystem.getSourceDataLine(audioFormat);
                    line.open(audioFormat, Note.SAMPLE_RATE);
                }
                line.start();
                toneStartTime = Instant.now();  // Record the start time

                byte[] toneData = Note.TONE.data(HamRadio.theRadio.getVolume());
                // Volume controlled by App.sound
                new Thread(() -> {
                    while (isPlaying.get()) {
                        line.write(toneData, 0, toneData.length);
                    }
                }).start();
            } catch (LineUnavailableException e) {
                System.out.println(e);
            }

        }
    }

    // Stop the audio with a minimum play duration (configurable)
    public void stopAudio() {
        if (isPlaying.get()) {
            long elapsedMillis = Duration.between(toneStartTime, Instant.now()).toMillis();

            if (elapsedMillis < minPlayTimeMillis) {
                // Delay stopping the audio to ensure it plays at least for minPlayTimeMillis
                new Thread(() -> {
                    try {
                        Thread.sleep(minPlayTimeMillis - elapsedMillis);  // Wait the remaining time
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    stopTone();
                }).start();
            } else {
                stopTone();  // Stop audio immediately if already played >minPlayTimeMillis
            }
        }
    }

    // Helper method to stop the tone
    private void stopTone() {
        if (isPlaying.getAndSet(false) && line != null && line.isOpen()) {
            line.stop();
            line.flush();
        }
    }

    // Close the audio line (when done using)
    public void close() {
        if (line != null) {
            line.close();
        }
    }

}

