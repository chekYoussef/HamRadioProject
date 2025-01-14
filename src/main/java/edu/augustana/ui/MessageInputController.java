package edu.augustana.ui;

import edu.augustana.dataModel.CWMessage;
import edu.augustana.helper.handler.MorseTranslator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MessageInputController {



    @FXML
    private TextField frequencyInput;

    @FXML
    private TextField messageInput;

    @FXML
    private TextField speedInput;

    @FXML
    private TextField toneInput;



    private edu.augustana.ui.HamController HamController; // Reference to the main controller

    public void setHamController(HamController HamController) {
        this.HamController = HamController;
    }

    @FXML
    private void saveMessage() {
        String message = messageInput.getText();
        String frequency = frequencyInput.getText();
        String WPM = speedInput.getText();
        String tone = toneInput.getText();


        // Validate frequency input
        try {
            double frequencyValue = Double.parseDouble(frequency);
            int wpmValue = Integer.parseInt(WPM);
            App.wpm = wpmValue;
            int toneValue = Integer.parseInt(tone);
            App.ditFrequency = toneValue;

            if (frequencyValue < HamController.minFrequency|| frequencyValue > HamController.maxFrequency) {
                // Handle invalid frequency
                System.out.println("Frequency out of range!");
                return;
            }

            // Save the message and frequency to the main controller
            HamController.receiveMessage(new CWMessage(MorseTranslator.instance.getMorseCodeForText(message), message, frequencyValue));
            // Close the input window
            Stage stage = (Stage) messageInput.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            System.out.println("Invalid frequency format!");
        }
    }
}
