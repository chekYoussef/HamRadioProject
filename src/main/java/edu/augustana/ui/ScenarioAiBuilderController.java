package edu.augustana.ui;

import edu.augustana.dataModel.AiBotDetails;
import edu.augustana.dataModel.AiScenarioData;
import edu.augustana.helper.handler.AiScenarioFileHandler;
import edu.augustana.data.AiScenarioPlayed;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class ScenarioAiBuilderController {

    @FXML
    private TextField scenarioNameField;
    @FXML
    private TextArea scenarioDescriptionArea;
    @FXML
    private TextArea scenarioNotesArea;

    @FXML
    private ListView<String> botListView;
    @FXML
    private Button addBotButton;
    @FXML
    private Button kickBotButton;

    @FXML
    private VBox botDetailsMenu;
    @FXML
    private TextField botNameField;
    @FXML
    private TextField botObjectiveField;
    @FXML
    private CheckBox isStartingBotCheckBox;

    @FXML
    private Button saveButton;
    @FXML
    private Button saveJsonButton;
    @FXML
    private Button openJsonButton;
    @FXML
    private Button playButton;
    @FXML
    private Button backButton;

    private final ArrayList<AiBotDetails> botDetailsList = new ArrayList<>();
    private int currentlyEditingBot = -1;

    private AiScenarioFileHandler fileHandler;

    @FXML
    public void initialize() {
        fileHandler = new AiScenarioFileHandler();
        AiScenarioPlayed.instance.clearData();

        // Handle bot selection
        botListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection.intValue() != -1) {
                loadBotDetails(newSelection.intValue());
            }
        });

        // Add bot button action
        addBotButton.setOnAction(event -> {
            if (botDetailsList.size() >= 5){
                // Display limit message
                showAlert("Limit reached","The limit for now is 5 bots per scenario.");
                botDetailsMenu.setVisible(false);
                return;
            }
            showBotDetailsMenu(-1);
        });

        // Kick bot button action
        kickBotButton.setOnAction(event -> removeSelectedBot());

        // Save bot details action
        saveButton.setOnAction(event -> saveBotDetails());



        botDetailsMenu.setVisible(false);

        // Save to JSON button action
        saveJsonButton.setOnAction(event -> {
            AiScenarioData scenarioData = new AiScenarioData(
                    scenarioNameField.getText(),
                    scenarioDescriptionArea.getText(),
                    scenarioNotesArea.getText(),
                    new ArrayList<>(botDetailsList)
            );
            AiScenarioFileHandler.exportToJson(scenarioData);
        });

        // Open JSON button action
        openJsonButton.setOnAction(event -> {
            AiScenarioData scenarioData = AiScenarioFileHandler.importFromJson();
            if (scenarioData != null) {
                scenarioNameField.setText(scenarioData.getName());
                scenarioDescriptionArea.setText(scenarioData.getDescription());
                scenarioNotesArea.setText(scenarioData.getNotes());
                botDetailsList.clear();
                botDetailsList.addAll(scenarioData.getBotsDetails());
            }
            updateBotListNames();


        });
    }

    private void showBotDetailsMenu(int botIndex) {
        botDetailsMenu.setVisible(true);

        if (botIndex == -1) {
            // Adding a new bot
            currentlyEditingBot = botIndex;
            botNameField.clear();
            botObjectiveField.clear();
            isStartingBotCheckBox.setSelected(false);
        } else {
            // Editing an existing bot
            currentlyEditingBot = botIndex;
            AiBotDetails details = botDetailsList.get(botIndex);
            if (details != null) {
                botNameField.setText(details.getName());
                botObjectiveField.setText(details.getObjective());
                isStartingBotCheckBox.setSelected(details.isStartingBot());
            }
        }
    }

    private void loadBotDetails(int botIndex) {
        showBotDetailsMenu(botIndex);
    }

    private void removeSelectedBot() {
        int selectedBot = botListView.getSelectionModel().getSelectedIndex();
        if (selectedBot != -1) {
            botDetailsList.remove(selectedBot);
        }
        updateBotListNames();
    }
    @FXML
    private void pressBackButton() throws IOException {
        App.setRoot("Menu");
    }
    @FXML
    private void pressPlayButton(ActionEvent event) throws IOException {
        if (botDetailsList.size() > 0){

            // Switch to the AiScenarioHamRadio scene
            AiScenarioPlayed.instance.setData(new AiScenarioData(scenarioNameField.getText(),scenarioDescriptionArea.getText(),scenarioNotesArea.getText(),botDetailsList));
            App.setRoot("AiScenarioHamRadio");
        }else{
            showAlert("Add bots","You need to have at least 1 bot to play the scenario.");
        }
    }
    private void saveBotDetails() {
        // Collect data from bot details fields
        String botName = botNameField.getText();
        String botObjective = botObjectiveField.getText();
        boolean isStartingBot = isStartingBotCheckBox.isSelected();

        if (botName.isEmpty()) {
            showAlert("Validation Error", "Bot Name cannot be empty.");
            return;
        }

        // Ensure only one starting bot
        if (isStartingBot) {
            for (int i = 0; i < botDetailsList.size(); i++) {
                AiBotDetails botDetail = botDetailsList.get(i);
                if (botDetail.isStartingBot() && i != currentlyEditingBot) {
                    showAlert("Validation Error", "Only one bot can be the starting bot.");
                    return;
                }
            }
        }

        // Update or add the bot
        AiBotDetails details = new AiBotDetails(botName, botObjective, isStartingBot);

        if (currentlyEditingBot != -1) {
            botDetailsList.set(currentlyEditingBot,details);
        }else{
            botDetailsList.add(details);
        }



        // Hide the bot details menu
        botDetailsMenu.setVisible(false);

        // Update the ListView selection
        updateBotListNames();
        if (currentlyEditingBot == -1 ){
            botListView.getSelectionModel().select(botDetailsList.size()-1);
        }
    }
    private void updateBotListNames(){
        // Clear all existing items from the ListView
        botListView.getItems().clear();

        // Create a new ObservableList to hold the bot names
        ObservableList<String> botNames = FXCollections.observableArrayList();

        // Loop through the botList and add each name to the ObservableList
        for (AiBotDetails bot : botDetailsList) {
            botNames.add(bot.getName());
        }

        // Set the new list of names to the ListView
        botListView.setItems(botNames);
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
