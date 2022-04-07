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

public class ChangeUsernameController {

    @FXML
    public TextField loginField;

    @FXML
    public TextField newUsernameField;
    @FXML
    public PasswordField passwordField;

    @FXML
    public Button cancelBtn;

    @FXML
    public Button changeBtn;

    private StartClient startClient;

    DBAuthenticationService auth = new DBAuthenticationService ();
    private Network network;


    @FXML
    public void initialize() {
        cancelBtn.setCancelButton (true);
        cancelBtn.setOnAction (event -> closeButtonAction());
        changeBtn.setOnAction (event -> {
            try {
                checkUsernameOnChangeInput ();
            } catch (IOException | SQLException | ClassNotFoundException e) {
                throw new RuntimeException (e);
            }
        });
    }

    public void checkUsernameOnChangeInput() throws IOException, SQLException, ClassNotFoundException {
        String login = loginField.getText ().trim ();
        String newUsername = newUsernameField.getText ().trim ();
        String password = passwordField.getText ().trim ();
        if (login.length () == 0 || password.length () == 0 || newUsername.length () == 0) {
            startClient.showErrorAlert ("Ошибка ввода при смене username", "Поля не должны быть пустыми");
        } else if (this.auth.checkPassword (password)) {
            startClient.showErrorAlert ("Ошибка пароля", "Пароль не соответствует");
        } else if (!this.auth.checkLogin (login)) {
            startClient.showErrorAlert ("Ошибка логина", "Выбранного пользователя не существует");
        } else {

            String oldUsername = this.auth.getUsernameByLoginAndPassword (login, password);
            this.auth.changeUsername (login, newUsername);
            network.sendChangeUsernameMessage(oldUsername, newUsername);
            this.closeButtonAction ();
        }
    }

    @FXML
    public void closeButtonAction(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
}
