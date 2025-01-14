package edu.augustana.helper.handler;

import edu.augustana.interfaces.callbacks.CallbackPress;
import edu.augustana.interfaces.callbacks.CallbackRelease;
import edu.augustana.ui.App;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.InputMismatchException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MorseHandler {

    private final TonePlayer tonePlayer = new TonePlayer(App.MIN_PLAY_TIME_SOUND);
    private ScheduledExecutorService schedulerLetter = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService schedulerWord = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService schedulerTone = Executors.newSingleThreadScheduledExecutor();
    private Runnable timerLetterTask;
    private Runnable timerToneTask;
    private Runnable timerWordTask;
    private static final MorseTranslator morseCodeTranslator = MorseTranslator.instance;  // Instance of MorseCodeTranslator

    private Instant keyPressTime;          // To store the time the space bar is pressed
    private StringBuilder userInput = new StringBuilder();
    private StringBuilder userInputLettersString = new StringBuilder();
    private final CallbackPress keypressCallback;
    private final CallbackRelease keyreleaseCallback;


    public MorseHandler(CallbackPress keypressCallback, CallbackRelease keyreleaseCallback, Node element) {
        this.keypressCallback = keypressCallback;
        this.keyreleaseCallback = keyreleaseCallback;
        element.setOnKeyPressed(this::handleKeyPress);
        element.setOnKeyReleased(this::handleKeyRelease);
    }

    public void handleKeyPress(KeyEvent event) {
        // Only respond to the space bar being pressed
        if (event.getCode() == KeyCode.SPACE && keyPressTime == null) {
                tonePlayer.startAudio();
                keypressCallback.onComplete();
            keyPressTime = Instant.now();

            if (timerLetterTask != null) {
                schedulerLetter.shutdownNow(); // Cancel any previously running timer
                schedulerLetter = Executors.newSingleThreadScheduledExecutor(); // Reset scheduler
            }
            if (timerWordTask != null) {
                schedulerWord.shutdownNow(); // Cancel any previously running timer
                schedulerWord = Executors.newSingleThreadScheduledExecutor(); // Reset scheduler
            }

        } else if (event.getCode() == KeyCode.M && keyPressTime == null) {
            tonePlayer.startAudio();
            timerToneTask = () -> Platform.runLater(()->{
                tonePlayer.stopAudio();
                keyPressTime = null;
                // Determine if the input is a dot or dash and append it to the userInput
                userInput.append("-");
                keyreleaseCallback.onComplete();
                // Create a new timer task that will run after the TIMER_DELAY
                timerLetterTask = () -> Platform.runLater(() -> {

                    // Try to check the Morse code after the timer finishes
                    try {
                        String letter = checkMorseCode();
                        userInputLettersString.append(letter);
                        keyreleaseCallback.onTimerComplete(letter);

                        this.clearUserInput();
                    } catch (InputMismatchException e) {
                        keyreleaseCallback.onTimerCatch(e);
                    }
                    //add timer for space between words

                });
                timerWordTask = () -> Platform.runLater(() -> {

                    // Try to check the Morse code after the timer finishes
                    userInputLettersString.append(" ");
                    keyreleaseCallback.onTimerWordComplete();
                });

                // Schedule the task after the specified delay
                schedulerLetter.schedule(timerLetterTask, App.TIMER_DELAY, TimeUnit.MILLISECONDS);
                schedulerWord.schedule(timerWordTask, App.TIMER_DELAY * 6, TimeUnit.MILLISECONDS);

            });
            schedulerTone.schedule(timerToneTask, App.DOT_THRESHOLD * 2, TimeUnit.MILLISECONDS);
            keypressCallback.onComplete();
            keyPressTime = Instant.now();

            if (timerLetterTask != null) {
                schedulerLetter.shutdownNow(); // Cancel any previously running timer
                schedulerLetter = Executors.newSingleThreadScheduledExecutor(); // Reset scheduler
            }
            if (timerWordTask != null) {
                schedulerWord.shutdownNow(); // Cancel any previously running timer
                schedulerWord = Executors.newSingleThreadScheduledExecutor(); // Reset scheduler
            }

        }else if (event.getCode() == KeyCode.N && keyPressTime == null) {
            tonePlayer.startAudio();
            timerToneTask = () -> Platform.runLater(()->{
                tonePlayer.stopAudio();
                keyPressTime = null;
                // Determine if the input is a dot or dash and append it to the userInput
                userInput.append(".");
                keyreleaseCallback.onComplete();
                // Create a new timer task that will run after the TIMER_DELAY
                timerLetterTask = () -> Platform.runLater(() -> {

                    // Try to check the Morse code after the timer finishes
                    try {
                        String letter = checkMorseCode();
                        userInputLettersString.append(letter);
                        keyreleaseCallback.onTimerComplete(letter);

                        this.clearUserInput();
                    } catch (InputMismatchException e) {
                        keyreleaseCallback.onTimerCatch(e);
                    }
                    //add timer for space between words

                });
                timerWordTask = () -> Platform.runLater(() -> {

                    // Try to check the Morse code after the timer finishes
                    userInputLettersString.append(" ");
                    keyreleaseCallback.onTimerWordComplete();
                });

                // Schedule the task after the specified delay
                schedulerLetter.schedule(timerLetterTask, App.TIMER_DELAY, TimeUnit.MILLISECONDS);
                schedulerWord.schedule(timerWordTask, App.TIMER_DELAY * 3, TimeUnit.MILLISECONDS);

            });

            schedulerTone.schedule(timerToneTask, App.DOT_THRESHOLD, TimeUnit.MILLISECONDS);
            keypressCallback.onComplete();
            keyPressTime = Instant.now();

            if (timerLetterTask != null) {
                schedulerLetter.shutdownNow(); // Cancel any previously running timer
                schedulerLetter = Executors.newSingleThreadScheduledExecutor(); // Reset scheduler
            }
            if (timerWordTask != null) {
                schedulerWord.shutdownNow(); // Cancel any previously running timer
                schedulerWord = Executors.newSingleThreadScheduledExecutor(); // Reset scheduler
            }

        }
    }

    private void handleKeyRelease(KeyEvent event) {
        // Only respond to the space bar being released
        if (event.getCode() == KeyCode.SPACE && keyPressTime != null) {
            // Calculate how long the space bar was held down
            Instant end = Instant.now();
            Duration duration = Duration.between(keyPressTime, end);
            long elapsedMillis = duration.toMillis();
            keyPressTime = null;
            tonePlayer.stopAudio();
            // Determine if the input is a dot or dash and append it to the userInput
            if (elapsedMillis < App.DOT_THRESHOLD) {
                userInput.append(".");
            } else {
                userInput.append("-");
            }
            keyreleaseCallback.onComplete();
            // Create a new timer task that will run after the TIMER_DELAY
            timerLetterTask = () -> Platform.runLater(() -> {

                // Try to check the Morse code after the timer finishes
                try {
                    String letter = checkMorseCode();
                    userInputLettersString.append(letter);
                    keyreleaseCallback.onTimerComplete(letter);

                    this.clearUserInput();
                } catch (InputMismatchException e) {
                    keyreleaseCallback.onTimerCatch(e);
                }
                //add timer for space between words

            });
            timerWordTask = () -> Platform.runLater(() -> {

                // Try to check the Morse code after the timer finishes
                userInputLettersString.append(" ");
                keyreleaseCallback.onTimerWordComplete();
            });

            // Schedule the task after the specified delay
            schedulerLetter.schedule(timerLetterTask, App.TIMER_DELAY, TimeUnit.MILLISECONDS);
            schedulerWord.schedule(timerWordTask, App.TIMER_DELAY * 3, TimeUnit.MILLISECONDS);


        }
    }

    private String checkMorseCode() {
        return morseCodeTranslator.translateMorseCode(userInput.toString());
    }

    public void clearUserInput() {
        this.userInput = new StringBuilder();
    }

    public void clearUserInputLetters() {
        this.userInputLettersString = new StringBuilder();
    }

    public StringBuilder getUserInputLetters() {
        return this.userInputLettersString;
    }

    public StringBuilder getUserInput() {
        return this.userInput;
    }

}
