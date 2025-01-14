package edu.augustana.ui;

import edu.augustana.data.HamRadio;
import edu.augustana.dataModel.AIResponse;
import edu.augustana.dataModel.AiBotDetails;
import edu.augustana.dataModel.AiScenarioData;
import edu.augustana.dataModel.CWMessage;
import edu.augustana.helper.handler.GeminiAiHandler;
import edu.augustana.data.AiScenarioPlayed;
import edu.augustana.helper.handler.MorseTranslator;
import edu.augustana.interfaces.HamControllerCallback;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class AiScenarioHamController extends HamController implements HamControllerCallback {
    @FXML
    private VBox rootVBox;  // Matches the fx:id for the root VBox in LiveHamController's FXML
    private HamController hamController;
    private double frequency;
    private AiScenarioData scenarioData = AiScenarioPlayed.instance.getData();
    private GeminiAiHandler geminiAiHandler = new GeminiAiHandler();
    private ArrayList<String> botFrequencies = new ArrayList<>();
    private static Random randGen = new Random();
    private final ConcurrentHashMap<String, CompletableFuture<Void>> botTasks = new ConcurrentHashMap<>();

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


            if (scenarioData != null) {
                System.out.println("Received scenario data: " + scenarioData);
            } else {
                System.out.println("Scenario data is null!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String generateFrequency() {
        // Generate a random integer from 0 to 67 (inclusive)
        int frequencyDecimal = AiScenarioHamController.randGen.nextInt(68);

        // Construct the full frequency string with exactly 4 digits
        String frequency = "7.0" + (frequencyDecimal < 10 ? "0" + frequencyDecimal : frequencyDecimal);

        // Ensure the generated frequency is not already in use
        if (botFrequencies.contains(frequency)) {
            return generateFrequency(); // Recursively generate a new frequency
        }

        return frequency;
    }
    @Override
    public void onInitialize() {
        // Configure "Start" button functionality here
        hamController.gridPane.getChildren().remove(hamController.simulateReceivingBtn);
        ArrayList<AiBotDetails> botsDetails = scenarioData.getBotsDetails();
        for (int i = 0; i < botsDetails.size(); i++) {
            AiBotDetails botDetails = botsDetails.get(i);
            boolean isStartingFirst = botDetails.isStartingBot();
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            // Create UI components for bot details
            Text botNameTextEl = new Text();
            Text botFrequencyTextEl = new Text();
            botNameTextEl.setText(botDetails.getName());
            String botFrequency = generateFrequency();

            botFrequencies.add(botFrequency);
            botFrequencyTextEl.setText(botFrequency);


            hbox.getChildren().add(botNameTextEl);
            hbox.getChildren().add(botFrequencyTextEl);
            AiScenarioPlayed.instance.AIHandler.createSession(botFrequency, botDetails.getName(), botDetails.getObjective());

            if (isStartingFirst) {
                HamRadio.theRadio.setFrequency(Double.parseDouble(botFrequency));
//                Use Gemini AI to generate a scenario response
                AIResponse response = AiScenarioPlayed.instance.AIHandler.generateAIResponse(botFrequency, "start");

                hamController.receiveMessage(new CWMessage(MorseTranslator.instance.getMorseCodeForText(response.getMessage(), true), response.getMessage(), Double.parseDouble(botFrequency)));

                System.out.println(response.getMessage());
            }
            hamController.leftBottomSection.getChildren().add(hbox);

        }
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

        // Get the current frequency and filter settings
        double currentFrequency = HamRadio.theRadio.getFrequency();
        double filterRangeHz = HamRadio.theRadio.getFilter();

        // Iterate through bot frequencies
        for (String botFrequencyStr : botFrequencies) {
            double botFrequency = Double.parseDouble(botFrequencyStr);
            double filterRangeMHz = filterRangeHz / 1_000_000.0;

            // Check if the bot's frequency is within the filter range
            if (Math.abs(botFrequency - currentFrequency) <= filterRangeMHz / 2) {
                // Use CompletableFuture to process asynchronously
                try {
                    // Generate a response from AI for the bot
                    AIResponse response = AiScenarioPlayed.instance.AIHandler.generateAIResponse(botFrequencyStr, message);

                    // Send the message to the bot and play the response
                    hamController.receiveMessage(new CWMessage(
                            MorseTranslator.instance.getMorseCodeForText(response.getMessage(), true),
                            response.getMessage(),
                            botFrequency
                    ));

                    System.out.println("Message sent to bot at frequency: " + botFrequencyStr);
                } catch (Exception e) {
                    System.err.println("Error processing message for bot at frequency: " + botFrequencyStr);
                    e.printStackTrace();
                }
            } else {
                System.out.println("Bot at frequency " + botFrequencyStr + " is outside the filter range.");
            }
        }
    }

    @Override
    public void onFrequencyChanged(double newFrequency) {
        System.out.println("Frequency changed to: " + newFrequency);
        frequency = newFrequency;
    }
}