package edu.augustana.ui;

import java.io.IOException;

import edu.augustana.data.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class HomeController {

    @FXML
    private TextField usernameField; // fx:id for the TextField where the user types the username
    private UserSession user = UserSession.instance;
    @FXML
    private void switchToSecondary() throws IOException {
        // Capture the entered username from the TextField
        String username = usernameField.getText();

        // Save the username to the session before switching screens
        user.setUsername(username);

        // Pass the username to the next screen (Menu)
        App.setRoot("Menu");
    }

    @FXML
    public void initialize() {
        // Retrieve the saved username from the session
        String savedUsername = user.getUsername();

        // If a username was saved, set it in the TextField
        if (savedUsername != null) {
            usernameField.setText(savedUsername);
        }
    }
}
