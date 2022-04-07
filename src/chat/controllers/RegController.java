package chat.controllers;

import chat.StartClient;
import chat.models.Network;
import chat.server.authentication.DBAuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class RegController {
    @FXML
    public TextField loginField;

    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField passwordRetypeField;

    @FXML
    public Button cancelButton;

    @FXML
    public Button signUpButton;

    private StartClient startClient;

    DBAuthenticationService auth = new DBAuthenticationService ();
    private Network network;

    @FXML
    public void initialize() {
        cancelButton.setCancelButton (true);
        cancelButton.setOnAction (event -> closeButtonAction());
        signUpButton.setOnAction (event -> {
            try {
                checkRegInput ();
            } catch (IOException | SQLException | ClassNotFoundException e) {
                throw new RuntimeException (e);
            }
        });
    }

    // Проверка логина и пароля при регистрации
    @FXML
    public void checkRegInput() throws IOException, SQLException, ClassNotFoundException {
        String login = loginField.getText ().trim ();
        String username = usernameField.getText ().trim ();
        String password = passwordField.getText ().trim ();
        String passwordRetype = passwordRetypeField.getText ().trim ();

        if (login.length () == 0 || password.length () == 0 || passwordRetype.length () == 0 || username.length () == 0) {
            startClient.showErrorAlert ("Ошибка ввода при регистрации", "Поля не должны быть пустыми");
        } else if (!password.equals (passwordRetype)) {
            startClient.showErrorAlert ("Ошибка ввода пароля при регистрации", "Пароли не совпадают");
        } else if (this.auth.checkLogin (login)) {
            startClient.showErrorAlert ("Ошибка логина при регистрации", "Выбранный логин уже занят");
        } else if (this.auth.checkUsername (username)) {
            startClient.showErrorAlert ("Ошибка username при регистрации", "Выбранный username уже занят");
        } else {
            this.auth.addNewClient (login, password, username);
            startClient.closeRegDialog ();
            startClient.openAuthDialog ();
        }
    }

    public void setStartClient(StartClient startClient) {
        this.startClient = startClient;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
    @FXML
    public void closeButtonAction(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
