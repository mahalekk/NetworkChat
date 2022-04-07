package chat;

import chat.controllers.ChangeUsernameController;
import chat.controllers.ChatController;
import chat.controllers.AuthController;
import chat.controllers.RegController;
import chat.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private Stage regStage;


    private Stage usnmStage;
    private ChatController chatController;

    @Override
    public void start(Stage stage) throws IOException {

        this.primaryStage = stage;

        network = new Network ();
        network.connect ();

        openAuthDialog ();
        createChatDialog ();

    }
    public void openAuthDialog () throws IOException {
        FXMLLoader authLoader = new FXMLLoader (StartClient.class.getResource ("resources/auth-view.fxml"));
        authStage = new Stage ();
        createStage(authStage, authLoader, "Authentication");
        AuthController chatController = authLoader.getController ();
        chatController.setNetwork (network);
        chatController.setStartClient (this);
    }

    public void openRegDialog() throws IOException {
        authStage.close ();
        FXMLLoader regLoader = new FXMLLoader (StartClient.class.getResource ("resources/reg-view.fxml"));
        regStage = new Stage ();
        createStage (regStage, regLoader, "Registration");
        RegController regController = regLoader.getController ();
        regController.setNetwork (network);
        regController.setStartClient (this);
    }

    public void closeRegDialog () {
        regStage.close();
    }

    // Выделил в отдельный метод, чтобы не дублировать
    private void createStage(Stage stage, FXMLLoader loader, String title) throws IOException {
        Scene scene = new Scene (loader.load ());
        stage.setScene (scene);
        stage.initModality (Modality.WINDOW_MODAL);
        stage.initOwner (primaryStage);
        stage.setTitle (title);
        stage.setY (1500);
        stage.setX (1000);
        stage.setAlwaysOnTop (true);
        stage.show ();
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
        chatController.setStartClient(this);
    }

    public void showErrorAlert(String title, String errorMessage) {
        Alert alert = new Alert (Alert.AlertType.ERROR);
        alert.setTitle (title);
        alert.setHeaderText (errorMessage);
        alert.show ();
    }

    public void openChangeUsernameDialog() throws IOException {
        FXMLLoader changeUsernameLoader = new FXMLLoader (StartClient.class.getResource ("resources/changeUsername-view.fxml"));
        usnmStage = new Stage ();
        createStage (usnmStage, changeUsernameLoader, "Change username");
        ChangeUsernameController changeController = changeUsernameLoader.getController ();
        changeController.setNetwork (network);
    }
}
