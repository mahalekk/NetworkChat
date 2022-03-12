package chat.controllers;

import chat.models.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {

    @FXML
    private ListView<String> usersList;

    @FXML
    private Label usernameTitle;

    @FXML
    private TextArea chatHistory;

    @FXML
    private TextField textInputField;

    @FXML
    private Button sendButton;

    @FXML
    public void initialize() {
        usersList.setItems(FXCollections.observableArrayList("Тимофей", "Дмитрий", "Диана", "Арман"));
        sendButton.setOnAction(event -> sendMessage());
        textInputField.setOnAction(event -> sendMessage());
    }

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    private void sendMessage() {
        String message = textInputField.getText().trim();
        textInputField.clear();

        if (message.trim().isEmpty()) {
            return;
        }
        network.sendMessage(message);
        appendMessage(message);
    }

    public void appendMessage(String message) {
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
    }


}
