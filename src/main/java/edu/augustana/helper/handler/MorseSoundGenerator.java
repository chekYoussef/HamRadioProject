package edu.augustana.helper.handler;

import edu.augustana.data.Note;
import edu.augustana.ui.App;

import javax.sound.sampled.LineUnavailableException;

public class MorseSoundGenerator {

    public static void playMorseCode(String morseCode, int wpm){
        try {
            playMorseCode(morseCode,wpm,App.ditFrequency);

        } catch (LineUnavailableException e) {
            System.out.println(e);
        }
    }
    public static void playMorseCode(String morseCode, int wpm, int ditPitch)
            throws LineUnavailableException {
        // Calculate durations for dit and dash based on character WPM
        int speed1WPM = 1200;
       
        int dotTime =  App.MIN_PLAY_TIME_SOUND; // Formula for dot duration in milliseconds

        // Set frequency for the tone
        Note.TONE.setFrequency(ditPitch);

        TonePlayer tonePlayer = new TonePlayer(dotTime);

        try {
            for (char symbol : morseCode.toCharArray()) {
                switch (symbol) {
                    case '.':
                        tonePlayer.startAudio();
                        Thread.sleep(dotTime); // Play "dot"
                        tonePlayer.stopAudio();
                        Thread.sleep(dotTime); // Pause between symbols in a character
                        break;
                    case '-':
                        tonePlayer.startAudio();
                        Thread.sleep(dotTime * 2L); // Play "dash"
                        tonePlayer.stopAudio();
                        Thread.sleep(dotTime); // Pause between symbols in a character
                        break;
                    case ' ':
                        Thread.sleep(speed1WPM / wpm * 3L); // Pause for a space (between words)
                        break;
                    case '%':
                        Thread.sleep(speed1WPM / wpm * 7L); // Pause for a space (between words)
                        break;
                    default:
                        System.out.println("Invalid character in Morse code input: " + symbol);
                        break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Morse code playback interrupted.");
        } finally {
            tonePlayer.close(); // Release resources
        }
    }
}
