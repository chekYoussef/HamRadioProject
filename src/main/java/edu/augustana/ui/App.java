package edu.augustana.ui;
import com.google.gson.Gson;
import edu.augustana.dataModel.CWMessage;
import edu.augustana.data.HamRadio;
import edu.augustana.helper.handler.MorseTranslator;
import edu.augustana.helper.handler.StaticNoisePlayer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;

/**
 * JavaFX App
 */
@ClientEndpoint
public class App extends Application {



    public static long TIMER_DELAY = 400;
    public static long DOT_THRESHOLD = 150;
    private static Scene scene;
    private static Stage stage; // Add this line
    public static int wpm = 20;
    public static final int MIN_PLAY_TIME_SOUND = 50;
    public static int ditFrequency = 600;

    //For server
    private static App app;
    private Session webSocketSession = null;

    @Override
    public void start(Stage stage) throws IOException {
        App.app=this;
        scene = new Scene(loadFXML("Home"), 800, 800);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            StaticNoisePlayer.stopNoise();
            Platform.exit();
            // Optionally, you can add other cleanup code here
        });
    }

    //Closing websocket
    @Override
    public void stop() {
        if (isConnectedToServer()) {
            try {
                webSocketSession.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/edu/augustana/"+fxml + ".fxml"));
        return fxmlLoader.load();
    }


    // Methods for server
    public static void connectToServer(String serverIPAddress, String userName) {
        try {
            if (isConnectedToServer()) {
                app.webSocketSession.close();
            }
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            String url = "ws://"+serverIPAddress+":8000/ws/"+userName;
            System.out.println("URL: "+ url);
            app.webSocketSession = container.connectToServer(app, new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Error connecting to server! " + e.getMessage()).show());
        }
    }
    public static boolean isConnectedToServer() {
        return app.webSocketSession != null && app.webSocketSession.isOpen();
    }

    // Commenting out this method because it is not adapted yet to our project but
    // I am putting it here to think should we add a HamMessage class that would have the
    // string + frequency of message
    public static void sendMessageToServer(CWMessage msg) {
        if (isConnectedToServer()) {
            String jsonMessage = new Gson().toJson(msg);
            System.out.println("DEBUG: Sending WebSocket message: " + jsonMessage);
            app.webSocketSession.getAsyncRemote().sendText(jsonMessage);
        }
    }

    @OnMessage
    public void onMessage(String jsonMessage) {
        System.out.println("DEBUG: Received WebSocket message: " + jsonMessage);
        CWMessage cwMessage = new Gson().fromJson(jsonMessage, CWMessage.class);
        cwMessage = new CWMessage(MorseTranslator.instance.getMorseCodeForText(cwMessage.getCwText(),true),cwMessage.getCwText(),cwMessage.getFrequency());
        HamRadio.theRadio.receiveMessage(cwMessage);
//        ADD HANDLING RECEIVING MESSAGE
    }


    public static void main(String[] args) {
        launch();
    }

}