package chat.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatController {

    @FXML
    private TextArea textInputField;

    @FXML
    private TextArea commonTextPanel;

    Date date = new Date ();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @FXML
    void pushTextToPanel() {
        String message = textInputField.getText ().trim ();

        if (message.length () != 0) {
            commonTextPanel.appendText (message + " " + formatter.format (date) + "\n");
            textInputField.clear ();
        } else {
            Alert alert = new Alert (Alert.AlertType.ERROR);
            alert.setTitle ("Ошибка ввода!!");
            alert.setHeaderText ("Текст не должен быть пустой");
            alert.show ();
        }
    }

    @FXML
    void exitFromChat() {
        Platform.exit ();
    }

    @FXML
    void showAbout() {
        Alert alert = new Alert (Alert.AlertType.INFORMATION);
        alert.setTitle ("Информация о разработчике");
        alert.setContentText ("Данный чат был разработан в рамказ учебной программы факультета Java-разработки Беймишевым А.С.");
        alert.show ();
    }
}
