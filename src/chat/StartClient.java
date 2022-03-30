package chat;

import chat.controllers.ChatController;
import chat.controllers.AuthController;
import chat.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import java.io.IOException;

/**
 * JavaFX App
 */
public class StartClient extends Application {

    private Network network;
    private Stage primaryStage;
    private Stage authStage;
    private ChatController chatController;

    @Override
    public void start(Stage stage) throws IOException {

        this.primaryStage = stage;

        network = new Network ();
        network.connect ();

        openAuthDialog ();
        createChatDialog ();

    }
    private void openAuthDialog () throws IOException {
        FXMLLoader authLoader = new FXMLLoader (StartClient.class.getResource ("resources/auth-view.fxml"));
        authStage = new Stage ();
        Scene scene = new Scene (authLoader.load ());

        authStage.setScene (scene);
        authStage.initModality (Modality.WINDOW_MODAL);
        authStage.initOwner (primaryStage);
        authStage.setTitle ("Authentication");
        authStage.setY (1500);
        authStage.setX (1000);
        authStage.setAlwaysOnTop (true);
        authStage.show ();

        AuthController chatController = authLoader.getController ();
        chatController.setNetwork (network);
        chatController.setStartClient (this);
    }
    private void createChatDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource("resources/chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load (), 600,400);
        primaryStage.setScene(scene);
        primaryStage.setY(1500);
        primaryStage.setX(1000);
        primaryStage.setAlwaysOnTop (true);
        chatController = fxmlLoader.getController ();
        chatController.setNetwork (network);
    }
    public static void main(String[] args) {
        launch();
    }

    public void openChatDialog() {
        authStage.close ();
        primaryStage.show ();
        primaryStage.setTitle (network.getUsername ());
        network.waitMessage (chatController);
        chatController.setUsernameTitle (network.getUsername ());
    }

    public void showErrorAlert(String title, String errorMessage) {
        Alert alert = new Alert (Alert.AlertType.ERROR);
        alert.setTitle (title);
        alert.setHeaderText (errorMessage);
        alert.show ();
    }
}
