package edu.augustana.ui;

import edu.augustana.dataModel.CWMessage;
import edu.augustana.interfaces.HamControllerCallback;
import edu.augustana.interfaces.callbacks.CallbackPress;
import edu.augustana.interfaces.callbacks.CallbackRelease;
import edu.augustana.data.HamRadio;
import edu.augustana.helper.handler.MorseHandler;
import edu.augustana.helper.handler.MorseSoundGenerator;
import edu.augustana.helper.handler.MorseTranslator;
import edu.augustana.helper.handler.StaticNoisePlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.InputMismatchException;


public class HamController {
    @FXML
    private Label chosenFrequency;
    @FXML
    private Label userMessageMorse;
    @FXML
    private Label userMessageInEnglish;
    @FXML
    private CheckBox showEnglishText;
    @FXML
    public GridPane gridPane;
    @FXML
    private Slider frequencySlider;
    @FXML
    public Button simulateReceivingBtn;
    @FXML
    private Slider filterSlider;
    @FXML
    private Slider speedSlider;
    @FXML
    private Button returnMenuButton;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Slider volumeSlider;
    @FXML
    public VBox leftBottomSection;

    private final int oneMillion = 1000000;
    private HamControllerCallback callback;


    // things to put in new Class MorseHandler
    private String frequencyUnit = " Mhz";
    public final double minFrequency = 7.000;
    public final double maxFrequency = 7.067;
    private final double initialFrequency = 7.035;
    private boolean isInit = false;
    private boolean isInitCallback = false;
    private MorseHandler morseHandler;
    double truncatedFrequency;
    private double transmittedFrequency;

    public void initialize() {

        HamRadio.theRadio.setNewMessageListener(this::receiveMessage);
        HamRadio.theRadio.setFrequencyListener(frequency -> frequencySlider.setValue(frequency));

        frequencySlider.setMin(minFrequency);
        frequencySlider.setMax(maxFrequency);
        frequencySlider.setValue(initialFrequency);
        frequencySlider.valueProperty().addListener((obs, oldVal, newVal) -> HamRadio.theRadio.setFrequency(newVal.doubleValue()));;
        filterSlider.valueProperty().addListener((obs, oldVal, newVal) -> HamRadio.theRadio.setFilter(newVal.intValue()));;
        HamRadio.theRadio.setFrequency(frequencySlider.getValue());
        HamRadio.theRadio.setFilter((int) filterSlider.getValue());

        speedSlider.setValue(App.wpm);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            App.wpm = newVal.intValue();  // Update app-wide WPM setting
        });


        truncatedFrequency = Math.floor(frequencySlider.getValue() * 10000) / 10000;
        chosenFrequency.setText(String.format("%.3f", truncatedFrequency) + frequencyUnit);
        userMessageMorse.setText("Your message will be shown here");

        showEnglishText.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                userMessageInEnglish.setText("");
            }
            userMessageMorse.requestFocus();
        });

        morseHandler = new MorseHandler(new CallbackPress() {
            @Override
            public void onComplete() {

            }
        }, new CallbackRelease() {
            @Override
            public void onComplete() {
                // Update the UI to show the user's Morse code input so far
                String morseCodeSoFar = morseHandler.getUserInput().toString();
                userMessageMorse.setText(morseCodeSoFar);
                char character = morseCodeSoFar.charAt(morseCodeSoFar.length() - 1);
                if (callback != null) {
                    callback.onDitDahProcessed(character);
                }
            }

            @Override
            public void onTimerComplete(String letter) {
                StringBuilder userInputLettersString = morseHandler.getUserInputLetters();
                if (userInputLettersString.length() > 40) {
                    userInputLettersString.deleteCharAt(0);
                }
                char character = userInputLettersString.charAt(userInputLettersString.length() - 1);
                if (callback != null) {
                    callback.onCharacterProcessed(character);
                }

                userMessageMorse.setText("");
                if (showEnglishText.isSelected()) {
                    int length = userInputLettersString.length();
                    userMessageInEnglish.setText(userInputLettersString.substring(Math.max(0, length - 40), length));
                } else {
                    userMessageInEnglish.setText("");
                }

            }

            @Override
            public void onTimerWordComplete() {
                StringBuilder userInputLetters = morseHandler.getUserInputLetters();
                if (showEnglishText.isSelected()) {
                    userMessageInEnglish.setText(userInputLetters.toString());
                } else {
                    userMessageInEnglish.setText(""); // Clear text when checkbox is unchecked
                }
                String message = userInputLetters.toString();
                if (callback != null) {
                    callback.onMessageCompleted(message);
                }
                morseHandler.clearUserInputLetters();
            }

            @Override
            public void onTimerCatch(InputMismatchException e) {
                System.out.println(e.getMessage());
                morseHandler.clearUserInput();
                morseHandler.clearUserInputLetters();
                userMessageMorse.setText("");

            }

        }, borderPane);

        frequencySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            truncatedFrequency = Math.floor(frequencySlider.getValue() * 10000) / 10000;
            chosenFrequency.setText(String.format("%.3f", truncatedFrequency) + frequencyUnit);
            if (callback != null) {
                callback.onFrequencyChanged(newValue.doubleValue());
            }
        });

        // this part is for simulating static noise
        try {

            StaticNoisePlayer.startNoise();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }


        volumeSlider.adjustValue((double) HamRadio.theRadio.getVolume());
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int newVolume = newValue.intValue();
            HamRadio.theRadio.setVolume(newVolume);  // Update the volume variable

            // Scaling the slider value to the right range for StaticNoise
            float scaledVolume = (newVolume / 100.0f)  / 5.0f;;
            StaticNoisePlayer.setVolume(scaledVolume); // Update static noise volume

        });
        userMessageMorse.requestFocus();
        if (callback != null && isInitCallback == false) {
            callback.onInitialize();
            isInitCallback = true;
        }
        isInit = true;

    }

    public void setCallback(HamControllerCallback callback) {
        this.callback = callback;
        if (isInit == true && isInitCallback == false) {
            callback.onInitialize();
            isInitCallback = true;
        }

    }


    @FXML
    private void handleTranslationCheckBoxSelected() {

    }


    // this should open new window where user can input sentence that will be received
    // and enter a double that will be frequency of sender
    @FXML
    private void simulateReceiving() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/augustana/MessageInput.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 580, 800));

            // Pass reference to the main controller
            MessageInputController controller = loader.getController();
            controller.setHamController(this);

            stage.setTitle("Input Message and Frequency");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to check if slider frequency matches transmitted frequency and play sound
    private void checkFrequencyAndPlaySound(double currentFrequency) {
        if (currentFrequency == transmittedFrequency) {
            playCWsound();  // Method to play the CW sound
        } else {
            stopCWsound();  // Stop the sound if the frequencies don't match
        }
    }


    // Method to play the CW sound
    private void playCWsound() {
        // logic for generating and playing the CW (Morse) sound


        System.out.println("Playing CW sound at the correct frequency!");
    }

    // Method to stop the CW sound
    private void stopCWsound() {
        // Logic to stop sound playback
        System.out.println("Stopping CW sound since frequency does not match.");
    }



    private boolean isFrequencyWithinFilterRange(double messageFrequencyMHz, double radioFrequencyMHz, int filterHz) {
        // Convert MHz to Hz
        double messageFrequencyHz = messageFrequencyMHz * 1_000_000;
        double radioFrequencyHz = radioFrequencyMHz * 1_000_000;

        // Calculate the filter range
        double lowerBound = radioFrequencyHz - (filterHz / 2.0);
        double upperBound = radioFrequencyHz + (filterHz / 2.0);

        // Check if the message frequency is within the range
        if (messageFrequencyHz >= lowerBound && messageFrequencyHz <= upperBound) {
            return true; // Frequency is within range
        } else {
            return false; // Frequency is out of range
        }
    }

    // Method to receive and display the message and frequency from the new screen
    public void receiveMessage(CWMessage cwMessage) {
        int WPM = (int) speedSlider.getValue();
        String morseMessage = cwMessage.getCwText();
        transmittedFrequency = cwMessage.getFrequency();  /// Later note: Check the frequency and filter, instead of setting it to the current frequency.
        int filter = HamRadio.theRadio.getFilter();
        double radioFrequency = HamRadio.theRadio.getFrequency();
        if (isFrequencyWithinFilterRange(transmittedFrequency, radioFrequency, filter)) {
            userMessageMorse.setText("Received: " +  cwMessage.getOriginalMessage());
            try {
                System.out.println(WPM);
                App.wpm = WPM;
                MorseSoundGenerator.playMorseCode(morseMessage, WPM, (int) (App.ditFrequency - (HamRadio.theRadio.getFrequency() * oneMillion - transmittedFrequency * oneMillion)));
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    void SwitchMenuButton(ActionEvent event) throws IOException {
        App.setRoot("Menu");// Assuming App.setRoot is a static method to change scenes
        StaticNoisePlayer.stopNoise();
    }
}
