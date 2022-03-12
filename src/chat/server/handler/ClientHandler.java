package chat.server.handler;

import chat.server.Server;
import chat.server.authentication.AuthenticationService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cMsg"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/sMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pMsg"; // + msg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end";

    private Server server;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;
    private ClientHandler recipient;

    public ClientHandler(Server myServer, Socket socket) {

        this.server = myServer;
        clientSocket = socket;
    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
                    break;
                }

            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
                System.out.println("Неудачная попытка аутентификации");
            }
        }
    }

    private boolean processAuthentication(String message) throws IOException {
        String[] parts = message.split("\\s+");
        if (parts.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
        }
        String login = parts[1];
        String password = parts[2];

        AuthenticationService auth = server.getAuthenticationService();

        username = auth.getUsernameByLoginAndPassword(login, password);

        if (username != null) {
            if (server.isUsernameBusy(username)) {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Логин уже используется");
                return false;
            }

            out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);
            server.subscribe(this);
            System.out.println("Пользователь " + username + " подключился к чату");
            return true;
        } else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Логин или пароль не соответствуют действительности");
            return false;
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("message | " + username + ": " + message);
            if (message.startsWith(STOP_SERVER_CMD_PREFIX)) {
                System.exit(0);
            } else if (message.startsWith(END_CLIENT_CMD_PREFIX)) {
                return;
            } else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                server.broadcastPrivateMessage (this.getRecipientName(message), message, this);
            } else {
                server.broadcastMessage(message, this);
            }

        }
    }

    private String getRecipientName (String message) {
        String[] name = message.split ("\\s+");
        return name[1];
    }
    public void sendMessage(String sender, String message) throws IOException {
        out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
    }

    public void sendPrivateMessage(String sender, String message) throws IOException {
        out.writeUTF(String.format("%s %s %s", PRIVATE_MSG_CMD_PREFIX, sender, message));
    }

    public String getUsername() {
        return username;
    }
}
