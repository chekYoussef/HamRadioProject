package edu.augustana.helper.handler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.augustana.dataModel.AiScenarioData;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.*;
import java.lang.reflect.Type;

public class AiScenarioFileHandler {

    public static void exportToJson(AiScenarioData scenarioData) {
        Gson gson = new Gson();

        // Create a file chooser to save the JSON file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Scenario as JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(scenarioData, writer);
                showAlert("Success", "Scenario saved successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to save scenario: " + e.getMessage());
            }
        }
    }

    public static AiScenarioData importFromJson() {
        Gson gson = new Gson();

        // Create a file chooser to open a JSON file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Scenario JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (FileReader reader = new FileReader(file)) {
                // Define the type of the data to deserialize
                Type scenarioDataType = new TypeToken<AiScenarioData>() {}.getType();
                AiScenarioData scenarioData = gson.fromJson(reader, scenarioDataType);
                showAlert("Success", "Scenario loaded successfully!");
                return scenarioData;


            } catch (IOException e) {
                showAlert("Error", "Failed to load scenario: " + e.getMessage());
                return null;
            }
        }else{
            return null;
        }
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
