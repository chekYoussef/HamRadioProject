package edu.augustana.ui;


import edu.augustana.data.HamRadio;
import edu.augustana.dataModel.*;
import edu.augustana.data.Scenarios.ScenarioBots.DataManager;
import edu.augustana.helper.handler.MorseTranslator;
import edu.augustana.interfaces.HamControllerCallback;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ScenarioHamController extends HamController implements HamControllerCallback {
    @FXML
    private VBox rootVBox;  // Matches the fx:id for the root VBox in LiveHamController's FXML
    private HamController hamController;
    private static ScenarioData scenarioData;
    private double frequency;
    private ScriptedBot currentBot;
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/augustana/HamRadio.fxml"));

            // Load HamRadio.fxml as a BorderPane
            BorderPane hamInterface = loader.load();

            // Retrieve the HamController instance
            hamController = loader.getController();

            // Set the callback or perform other configuration with hamController
            hamController.setCallback(this);

            // Add hamInterface to rootVBox
            rootVBox.getChildren().add(hamInterface);

            // Retrieve scenario data but do NOT call startBtn() here
            scenarioData = DataManager.getInstance().getScenarioData();

            if (scenarioData != null) {
                System.out.println("Received scenario data: " + scenarioData);
            } else {
                System.out.println("Scenario data is null!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setScenarioData(ScenarioData data) {
        scenarioData = data;
        System.out.println("Received scenario data: " + scenarioData);
    }

    public static ScenarioData getScenarioData() {
        return scenarioData;
    }

    private void startBtn() {
        if (scenarioData != null) {
            String scenario = scenarioData.getSynopsis();
            int duration = (int) scenarioData.getDuration(); // Duration in minutes
            ScriptedBot scriptedBot = new ScriptedBot(scenario); // Create a ScriptedBot for the scenario

            // Iterate over the duration of the scenario and receive messages each minute
            this.currentBot = scriptedBot;
                ScriptedMessage scriptedMessage = scriptedBot.getNewMessage(); // Get the message at the current time (minute)
                frequency = HamRadio.theRadio.getFrequency();
                if (scriptedMessage != null) {
                    String message = scriptedMessage.getMessage(); // Get the message content
                    hamController.receiveMessage(new CWMessage(MorseTranslator.instance.getMorseCodeForText(message), message, frequency)); // Simulate receiving the message with the current frequency
                    System.out.println("Received message: " + message); // Log the message for debugging
                }

        } else {
            System.out.println("No scenario data provided!");
        }
    }


    @Override
    public void onInitialize() {
        // Configure "Start" button functionality here
        hamController.simulateReceivingBtn.setText("Start");
        hamController.simulateReceivingBtn.setOnAction((e) -> startBtn());
    }

    @Override
    public void onDitDahProcessed(char signalUnit) {
        System.out.println("Dit/Dah processed: " + signalUnit);
    }

    @Override
    public void onCharacterProcessed(char character) {
        System.out.println("Character processed: " + character);
    }

    @Override
    public void onMessageCompleted(String message) {
        System.out.println("Complete message received: " + message);
        if (isFrequencyWithinFilterRange(frequency,HamRadio.theRadio.getFrequency(),HamRadio.theRadio.getFilter())){
            ScriptedMessage scriptedMessage = currentBot.getNewMessage(); // Get the message at the current time (minute)
            if (scriptedMessage != null) {
                String messageBot = scriptedMessage.getMessage(); // Get the message content
                hamController.receiveMessage(new CWMessage(MorseTranslator.instance.getMorseCodeForText(messageBot), messageBot, frequency)); // Simulate receiving the message with the current frequency
                System.out.println("Received message: " + messageBot); // Log the message for debugging
            }
        }

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
    @Override
    public void onFrequencyChanged(double newFrequency) {
        System.out.println("Frequency changed to: " + newFrequency);
        frequency = newFrequency;
    }
}
