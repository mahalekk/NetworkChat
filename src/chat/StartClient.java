package chat;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

/**
 * JavaFX App
 */
public class StartClient extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource("resources/WorkController.fxml"));
        Scene scene = new Scene(fxmlLoader.load ());
        stage.setTitle ("Chat");
        stage.setScene(scene);
        stage.setY(1500);
        stage.setX(1000);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
