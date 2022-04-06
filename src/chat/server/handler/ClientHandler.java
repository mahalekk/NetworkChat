package chat.server.handler;

import chat.server.Server;
import chat.server.authentication.AuthenticationService;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ClientHandler {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cm"; // + msg
    private static final String CHANGE_USERNAME_PREFIX = "/change";
    private static final String SERVER_MSG_CMD_PREFIX = "/sm"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pm"; // + msg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end";
    private static final String GET_CLIENTS_CMD_PREFIX = "/clients";

    private Server server;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public ClientHandler(Server server, Socket socket) {

        this.server = server;
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
                server.unSubscribe (this);
                try {
                    server.disconnectClient(this);
                } catch (IOException exception) {
                    exception.printStackTrace ();
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException (e);
            }
        }).start();
    }


    private void authentication() throws IOException, SQLException, ClassNotFoundException {
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

    private boolean processAuthentication(String message) throws IOException, SQLException, ClassNotFoundException {
        String[] parts = message.split("\\s+");
        if (parts.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка аутентификации");
        }
        String login = parts[1];
        String password = parts[2];
        System.out.println (server.getAuthenticationService ().toString ());
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
            server.broadcastMessage (String.format (">>> %s подключился к чату", username ), this, true);
            server.connectClient (this);

            return true;
        } else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Логин или пароль не соответствуют");
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
                String[] parts = message.split ("\\s+", 3);
                String recipient = parts[1];
                String privateMessage = parts[2];
                server.broadcastPrivateMessage (recipient, privateMessage, this);
            } else if (message.startsWith(CHANGE_USERNAME_PREFIX)) {
                String[] parts = message.split ("\\s+", 3);
                String oldUsername = parts[1];
                String newUsername = parts[2];
                System.out.println ("acceptMessageOnClientHandler " + message);
                server.updateClientUsername (this, oldUsername, newUsername);
            } else {
                server.broadcastMessage(message,this);
            }

        }
    }

    public void sendMessage(String sender, String message) throws IOException {
        if (sender != null) {
            out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
        }
    }

    public void sendServerMessage(String message) throws IOException {
        out.writeUTF (String.format ("%s %s", SERVER_MSG_CMD_PREFIX, message));
    }


    public String getUsername() {
        return username;
    }

    public void sendUserList(List<ClientHandler> clients) throws IOException {
        String message = String.format ("%s %s", GET_CLIENTS_CMD_PREFIX, clients.toString ());
        out.writeUTF (message);
        System.out.println (message);
    }

    @Override
    public String toString() {
        return username;
    }
}
