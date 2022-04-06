package chat.controllers;

import chat.StartClient;
import chat.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

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
    private Button changeUsernameBtn;
    private String selectedRecipient;
    private StartClient startClient;

    @FXML
    public void initialize() {
        sendButton.setOnAction (event -> sendMessage ());
        changeUsernameBtn.setOnAction (event -> {
            try {
                changeUsername();
            } catch (IOException e) {
                throw new RuntimeException (e);
            }
        });
        textInputField.setOnAction (event -> sendMessage ());

        usersList.setCellFactory (lv -> {
            MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel ();
            ListCell<String> cell = new ListCell<> ();
            cell.textProperty().bind (cell.itemProperty ());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                usersList.requestFocus ();
                if (!cell.isEmpty ()) {
                    int index = cell.getIndex ();
                    if (selectionModel.getSelectedIndices ().contains (index)) {
                        selectionModel.clearSelection (index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select (index);
                        selectedRecipient = cell.getItem ();
                    }
                    event.consume ();
                }
            });
            return cell;
        });
    }

    public void changeUsername() throws IOException {
        startClient.openChangeUsernameDialog();

    }

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    private void sendMessage() {
        String message = textInputField.getText ().trim ();
        textInputField.clear ();

        if (message.trim ().isEmpty ()) {
            return;
        }

        if (selectedRecipient != null) {
            network.sendPrivateMessage (selectedRecipient, message);
        } else {
            network.sendMessage (message);
        }
        appendMessage ("Я: " + message);
    }

    public void appendMessage(String message) {
        String timestamp = DateFormat.getInstance ().format (new Date ());

        chatHistory.appendText (timestamp);
        chatHistory.appendText (System.lineSeparator ());
        chatHistory.appendText (message);
        chatHistory.appendText (System.lineSeparator ());
        chatHistory.appendText (System.lineSeparator ());
    }

    public void appendServerMessage(String serverMessage) {
        chatHistory.appendText (serverMessage);
        chatHistory.appendText (System.lineSeparator ());
        chatHistory.appendText (System.lineSeparator ());
    }

    public void setUsernameTitle(String username) {
        this.usernameTitle.setText (username);
    }

    public void updateUsersList(String[] users) {
        Arrays.sort (users);
        for (int i = 0; i < users.length; i++) {
            if(users[i].equals(network.getUsername ())) {
                users[i] = ">>> " + users[i];
            }
            usersList.getItems ().clear ();
            Collections.addAll (usersList.getItems (), users);
        }
    }
    public void setStartClient(StartClient startClient) {
        this.startClient = startClient;
    }
}
