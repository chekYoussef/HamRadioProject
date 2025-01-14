package edu.augustana.helper.handler;

import com.google.gson.Gson;
import edu.augustana.dataModel.ScenarioData;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ScenarioFileHandler {
    public static void exportToJson(ScenarioData data) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Scenario as JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(null);

        Gson gson = new Gson();
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Create a container for scenario data
                gson.toJson(data, writer);
                showAlert("Success", "Scenario saved successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to save scenario: " + e.getMessage());
            }
        }


    }

    // Method to load scenario data from JSON
    public static ScenarioData importFromJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Scenario File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(file.getAbsolutePath())) {
                showAlert("Success", "Scenario loaded successfully!");

                return gson.fromJson(reader, ScenarioData.class);
            } catch (IOException e) {
                showAlert("Error", "Failed to load scenario: " + e.getMessage());
                e.printStackTrace();
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
