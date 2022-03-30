package chat.controllers;

import chat.StartClient;
import chat.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AuthController {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    private Network network;
    private StartClient startClient;

    @FXML
    public void checkAuth() throws IOException {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.length() == 0 || password.length() == 0) {
            startClient.showErrorAlert("Ошибка ввода при аутентификации", "Поля не должны быть пустыми");

            return;
        }

        String authErrorMessage = network.sendAuthMessage(login, password);

        if (authErrorMessage == null) {
            startClient.openChatDialog();
        } else {
            startClient.showErrorAlert("Ошибка аутентификации", authErrorMessage);
        }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }


    public void setStartClient(StartClient startClient) {
        this.startClient = startClient;
    }

}
