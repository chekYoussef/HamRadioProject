package edu.augustana.ui;

import edu.augustana.data.HamRadio;
import edu.augustana.helper.handler.MorseHandler;
import edu.augustana.helper.handler.MorseSoundGenerator;
import edu.augustana.helper.handler.MorseTranslator;
import edu.augustana.interfaces.callbacks.CallbackRelease;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ChoiceBox;
import java.util.*;
import java.io.IOException;
import java.util.Random;
import javafx.event.ActionEvent;
import javafx.scene.layout.BorderPane;

public class LevelController {
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label letterLabel;    // Label to display the random letter/word/phrase
    @FXML
    private Label definitionLabel; // Label to display the term definition
    @FXML
    private Label morseCodeLabel; // Label to display the user's current morse code
    @FXML
    private Label userInputLettersLabel;
    @FXML
    private ChoiceBox<String> levelChoiceBox; // ChoiceBox for selecting difficulty level
    @FXML
    private CheckBox showEnglishCheckbox; // Checkbox to toggle visibility
    @FXML
    private CheckBox playCorrectAudioCheckbox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider wpmSlider;
    private boolean isErrorState = false;
    private String currentText;       // The current random letter/word/phrase
    private String currentLevel = "Easy"; // Store the current level
    private boolean playCorrectAudio = true;
    private MorseHandler morseHandler;
    private final String[] words = {"NAME", "PWR", "FB", "73", "QSY?", "DE"}; // Example words
    private final String[] phrases = {"BT HW COPY?", "TNX FER CALL", "BT QTH IS"}; // Example phrases
    public Map<String, String> definitionsMap = new HashMap<>();
    private final MorseTranslator morseTranslator = MorseTranslator.instance; // Instance of MorseTranslator

    public void initialize() {
        /* Populate definitions */
        definitionsMap.put("NAME", "");
        definitionsMap.put("PWR", "transmission power");
        definitionsMap.put("FB", "fine business");
        definitionsMap.put("73", "best regards");
        definitionsMap.put("QRL?", "Are you busy?");
        definitionsMap.put("WX", "weather");
        definitionsMap.put("QSY?", "A question asking if the other party would like to change frequency.");
        definitionsMap.put("DE", "Abbreviation for 'from' in amateur radio communication.");
        definitionsMap.put("BT HW COPY?", "Asking how well the receiver can copy the message.");
        definitionsMap.put("TNX FER CALL", "Thanking the other operator for their call.");
        definitionsMap.put("BT QTH IS", "Asking for the other operator's location.");
        definitionLabel.setText("");
        // Populate the level choice box with levels
        levelChoiceBox.getItems().addAll("Easy", "Medium", "Hard");
        levelChoiceBox.setValue("Easy");  // Default selection
        playCorrectAudioCheckbox.setSelected(true);
        playCorrectAudioCheckbox.setOnAction((event -> handlePlayCorrectAudioCheckBox()));
        playCorrectAudioCheckbox.setFocusTraversable(false);
        userInputLettersLabel.visibleProperty().bind(showEnglishCheckbox.selectedProperty());
        showEnglishCheckbox.setFocusTraversable(false);
        morseHandler = new MorseHandler(() -> {
        }, new CallbackRelease() {

            @Override
            public void onComplete() {
                morseCodeLabel.setText(morseHandler.getUserInput().toString());
            }

            @Override
            public void onTimerComplete(String letter) {
                StringBuilder userInputLetters = morseHandler.getUserInputLetters();
                String userInputLettersString = userInputLetters.toString().trim();
                if (isErrorState) {
                    isErrorState = false; // Reset error state
                    userInputLettersLabel.setStyle("-fx-text-fill: green; -fx-font-size: 24px;"); // Restore default style
                }
                userInputLettersLabel.setText(userInputLettersString);
                if (!(currentText.indexOf(userInputLettersString) == 0)){
                    throw new InputMismatchException("Try again (incorrect input: " + letter + ")");
                }

            }

            @Override
            public void onTimerWordComplete() {
                StringBuilder userInputLetters = morseHandler.getUserInputLetters();
                String userInputLettersString = userInputLetters.toString().trim();

                if (!currentText.startsWith(userInputLettersString)) {
                    // Incorrect input
                    userInputLettersLabel.setStyle("-fx-text-fill: red; -fx-font-size: 24px;");
                } else {
                    // Update the label with the user's input
                    userInputLettersLabel.setText(userInputLettersString);

                    if (currentText.equals(userInputLettersString)) {
                        // Correct and complete input
                        userInputLettersLabel.setStyle("-fx-text-fill: green; -fx-font-size: 24px;");
                        Platform.runLater(() -> generateRandomText());
                        morseHandler.clearUserInputLetters();
                        morseHandler.clearUserInput();
                    } else {
                        // Partially correct input (still green)
                        userInputLettersLabel.setStyle("-fx-text-fill: green; -fx-font-size: 24px;");
                    }
                }
            }
            @Override
            public void onTimerCatch(InputMismatchException e) {
                isErrorState = true; // Enter error state

                // Display the error message with the incorrect answer
                String incorrectAnswer = morseHandler.getUserInputLetters().toString();
                userInputLettersLabel.setStyle("-fx-text-fill: red; -fx-font-size: 24px;");
                userInputLettersLabel.setText("Try again (incorrect input: " + incorrectAnswer + ")");

                // Clear the Morse code display
                morseCodeLabel.setText("");
                morseHandler.clearUserInput();
                morseHandler.clearUserInputLetters();
                // Play the correct Morse code to help the user retry
                playCorrectMorse(currentText);
            }


        }, borderPane);
        // Add a listener for level changes
        levelChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            changeDifficultyLevel(newValue);
            letterLabel.requestFocus();
            morseHandler.clearUserInputLetters();
            userInputLettersLabel.setText("Your input will appear here");
        });
        // Start with the appropriate level (default: Easy)
        generateRandomText();
        volumeSlider.adjustValue((double) HamRadio.theRadio.getVolume());
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            HamRadio.theRadio.setVolume(newValue.intValue());  // Update the volume variable
        });
        wpmSlider.adjustValue((double) App.wpm);
        wpmSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            App.wpm = newValue.intValue();  // Update App wpm with slider value
        });

    }

    private void handlePlayCorrectAudioCheckBox() {
        playCorrectAudio = playCorrectAudioCheckbox.isSelected();
    }

    // Adjust the difficulty level and update the display text accordingly
    private void changeDifficultyLevel(String level) {
        currentLevel = level;
        generateRandomText();  // Generate the appropriate text based on the selected level
    }

    private void playCorrectMorse(String text) {
        // Check if the checkbox is selected
        if (playCorrectAudioCheckbox.isSelected()) {
            String morse = morseTranslator.getMorseCodeForText(text); // Translate text to Morse code
            System.out.println("Playing correct Morse: " + morse);
                MorseSoundGenerator.playMorseCode(morse, App.wpm);
        } else {
            System.out.println("Skipped playing correct Morse audio as checkbox is not selected.");
        }
    }


    private void generateRandomText() {
        // Generate text based on the selected difficulty level
        userInputLettersLabel.setStyle("-fx-text-fill: green; -fx-font-size: 24px;");
        String beforeGenerate = currentText;
        switch (currentLevel) {
            case "Easy":
                generateRandomLetter();
                break;
            case "Medium":
                generateRandomWord();
                break;
            case "Hard":
                generateRandomPhrase();
                break;
        }
        // Play correct morse code
        playCorrectMorse(currentText);

        if (currentText.equals(beforeGenerate)) {
            generateRandomText();
        }
        // Set the definition if it's a word or phrase, or clear it if it's a single letter
        String definition = definitionsMap.getOrDefault(currentText, "");
        definitionLabel.setText(definition);
    }

    private void generateRandomLetter() {
        // Generate a random letter from A-Z
        Random random = new Random();
        char randomChar = (char) (random.nextInt(26) + 'A');
        currentText = String.valueOf(randomChar);

        // Set the random letter in the label
        letterLabel.setText(currentText);

        // Reset user input and update the morse code label
        morseHandler.clearUserInput();
    }

    private void generateRandomWord() {
        // Generate a random word from the predefined list
        Random random = new Random();
        currentText = words[random.nextInt(words.length)];

        // Set the random word in the label
        letterLabel.setText(currentText);

        // Reset user input and update the morse code label
        morseHandler.clearUserInput();
        morseCodeLabel.setText("");  // Clear the Morse code label for the new word
    }

    private void generateRandomPhrase() {
        // Generate a random phrase from the predefined list
        Random random = new Random();
        currentText = phrases[random.nextInt(phrases.length)];

        // Set the random phrase in the label
        letterLabel.setText(currentText);

        // Reset user input and update the morse code label
        morseHandler.clearUserInput();
        morseCodeLabel.setText("");  // Clear the Morse code label for the new phrase
    }

    @FXML
    void SwitchMenuButton(ActionEvent event) throws IOException {
        App.setRoot("Menu");
    }
}
