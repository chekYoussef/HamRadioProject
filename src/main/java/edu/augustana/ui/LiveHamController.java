package edu.augustana.ui;


import edu.augustana.data.HamRadio;
import edu.augustana.data.UserSession;
import edu.augustana.interfaces.HamControllerCallback;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LiveHamController implements HamControllerCallback {
    @FXML
    private VBox rootVBox;  // This should match the fx:id for the root VBox in LiveHamController's FXML

    private HamController hamController;
    private double frequency;
    public void initialize() {
        try {
            App.connectToServer("34.56.49.240", UserSession.instance.getUsername());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/augustana/HamRadio.fxml"));

            // Load HamRadio.fxml as a BorderPane
            BorderPane hamInterface = loader.load();

            // Retrieve the HamController instance
            hamController = loader.getController();

            // Set the callback or perform other configuration with hamController
            hamController.setCallback(this);

            // Add hamInterface to rootVBox
            rootVBox.getChildren().add(hamInterface);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDitDahProcessed(char signalUnit) {
        System.out.println("Dit/Dah processed: " + signalUnit);
    }
    @Override
    public void onInitialize(){
        hamController.gridPane.getChildren().remove(hamController.simulateReceivingBtn);


    }
    @Override
    public void onCharacterProcessed(char character) {
        System.out.println("Character processed: " + character);
        // Only send to server if needed for specific cases
    }

    @Override
    public void onMessageCompleted(String messageText) {

        System.out.println("Complete message received: " + messageText);
        HamRadio.theRadio.sendMessage(messageText);
    }

    @Override
    public void onFrequencyChanged(double newFrequency) {
        System.out.println("Frequency changed to: " + newFrequency);
        frequency = newFrequency;
        // Custom behavior after frequency change

    }

}
