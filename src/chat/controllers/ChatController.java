package chat.controllers;

//import chat.LogInfo;
import chat.StartClient;
import chat.models.Network;
import chat.server.Server;
import chat.server.ServerApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private String logDate;
    private String logMessage;
    private StartClient startClient;

    @FXML
    public void initialize() {
        sendButton.setOnAction (event -> {
            try {
                sendMessage ();
            } catch (IOException e) {
                throw new RuntimeException (e);
            }
        });
        changeUsernameBtn.setOnAction (event -> {
            try {
                changeUsername();
            } catch (IOException e) {
                throw new RuntimeException (e);
            }
        });
        textInputField.setOnAction (event -> {
            try {
                sendMessage ();
            } catch (IOException e) {
                throw new RuntimeException (e);
            }
        });

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
        this.getUserStory();
    }

    // метод для чтения истории чата
    private void getUserStory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ServerApp.getFile ()))) {
            String line;
            ArrayList<String> list = new ArrayList<> ();
            while ((line = reader.readLine()) != null) {
                if(line.length () != 0) {
                    list.add (line);
                }
            }

            //Обработка на случай если  в чате меньше 100 сообщений
            if (list.size () >= 100) {
                for (int i = list.size () - 100; i <= list.size () - 1; i++) {
                    chatHistory.appendText (list.get (i) + "\n");
                    chatHistory.appendText (System.lineSeparator ());
                }
            } else {
                for (int i = 0; i <= list.size () - 1; i++) {
                    chatHistory.appendText (list.get (i) + "\n");
                    chatHistory.appendText (System.lineSeparator ());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void changeUsername() throws IOException {
        startClient.openChangeUsernameDialog();

    }

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    private void sendMessage() throws IOException {
        String message = textInputField.getText ().trim ();
        textInputField.clear ();

        if (message.trim ().isEmpty ()) {
            return;
        }
        logDate = this.getLogDate () ;
        if (selectedRecipient != null) {
            network.sendPrivateMessage (selectedRecipient, message);
        } else {
            network.sendMessage (message);
            this.writeMessageToLog(message);
        }
        appendMessage (network.getUsername () + " " + message);
    }

    // выделил метод для форматирования времени
    private String getLogDate() {
        DateFormat dateFormat = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    // метод для записи сообщения в историю чата
    private void writeMessageToLog(String message) {
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ServerApp.getFile (), true))) {
            logMessage = logDate + " " + network.getUsername () + " " + message;
            bufferedWriter.write(System.lineSeparator ());
            bufferedWriter.write(logMessage + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendMessage(String message) {
        chatHistory.appendText (logDate + " ");
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
