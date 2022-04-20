package chat.models;

import chat.controllers.ChatController;
import chat.server.Server;
import javafx.application.Platform;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;


public class Network {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String CLIENT_MSG_CMD_PREFIX = "/cm"; // + msg
    private static final String GET_CLIENTS_CMD_PREFIX = "/clients";
    private static final String CHANGE_USERNAME_PREFIX = "/change";
    private static final String SERVER_MSG_CMD_PREFIX = "/sm"; // + msg
    private static final String NEW_CLIENT_REG_PREFIX = "/stop";
    private static final String CLIENT_OK_REG_PREFIX = "/regok";
    private static final String CLIENT_ERR_REG_PREFIX = "/regerr";

    private static final String PRIVATE_MSG_CMD_PREFIX = "/pm"; // + msg

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8180;
    private DataInputStream in;
    private DataOutputStream out;

    private String username;

    private final String host;
    private final int port;

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Server server;

    public Network() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
    }

    public void connect() {
        try {
            Socket socket = new Socket(host, port);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Соединение не установлено");
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при отправке сообщения");
        }
    }

    public void waitMessage(ChatController chatController) {
        Thread t = new Thread (() -> {
            try {
                while (true) {
                    String message = in.readUTF ();
                    if (message.startsWith (CLIENT_MSG_CMD_PREFIX)) {
                        String[] parts = message.split ("\\s+", 3);
                        String sender = parts[1];
                        String messageFromSender = parts[2];
                        Platform.runLater (() -> chatController.appendMessage (String.format ("%s: %s", sender, messageFromSender)));

                    } else if (message.startsWith (SERVER_MSG_CMD_PREFIX)) {
                        String[] parts = message.split ("\\s+", 2);
                        String serverMessage = parts[1];
                        Platform.runLater (() -> chatController.appendServerMessage (serverMessage));

                    } else if (message.startsWith (GET_CLIENTS_CMD_PREFIX)) {
                        System.out.println (message);
                        message = message.substring (message.indexOf ('[') + 1, message.indexOf (']'));
                        String[] users = message.split (", ");
                        System.out.println ("users " + Arrays.toString (users));
                        Platform.runLater (() -> chatController.updateUsersList (users));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace ();
            }
        });

        t.setDaemon (true);
        t.start ();

    }

    public String sendAuthMessage(String login, String password) {
        try {
            out.writeUTF (String.format ("%s %s %s", AUTH_CMD_PREFIX, login, password));
            String response = in.readUTF ();
            if (response.startsWith (AUTHOK_CMD_PREFIX)) {
                this.username = response.split ("\\s+", 2)[1];
                return null;
            } else {
                return response.split ("\\s+",2)[1];
            }
        } catch (IOException e) {
            e.printStackTrace ();
            return e.getMessage ();
        }
    }

    public void sendChangeUsernameMessage(String oldUsername, String newUsername) {
        try {
            out.writeUTF (String.format ("%s %s %s", CHANGE_USERNAME_PREFIX, oldUsername, newUsername));
            System.out.println ("sendChangeUserMessage " + oldUsername + " " + newUsername);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendPrivateMessage(String selectedRecipient, String message) {
        sendMessage (String.format ("%s %s %s", PRIVATE_MSG_CMD_PREFIX, selectedRecipient, message));
    }
}
