package chat.server;

import chat.server.authentication.AuthenticationService;
import chat.server.authentication.DBAuthenticationService;
import chat.server.handler.ClientHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class  Server {
    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;
    private final List<ClientHandler> clients;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authenticationService = new DBAuthenticationService ();
        clients = new ArrayList<> ();

    }

    public void start() {
        System.out.println("СЕРВЕР ЗАПУЩЕН!");
        System.out.println("----------------");

        try {
            while(true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        System.out.println("Ожидание клиента...");
        Socket socket = serverSocket.accept();
        System.out.println("Клиент подключился!");

        processClientConnection(socket);
    }

    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler handler = new ClientHandler(this, socket);
        handler.handle();
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }
    public synchronized void broadcastPrivateMessage (String recipient, String privateMessage, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername ().equals (recipient)) {
                client.sendMessage (sender.getUsername (), privateMessage);
            }
        }
    }
    public synchronized void broadcastMessage(String message, ClientHandler sender, boolean isServerMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(isServerMessage ? null : sender.getUsername(), message);
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        broadcastMessage(message, sender, false);
    }

    public void disconnectClient(ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendServerMessage (String.format ("Пользователь %s отключился", sender.getUsername ()));
            client.sendUserList (clients);
        }
    }

    public void connectClient (ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            client.sendServerMessage (String.format ("Пользователь %s подключился", sender.getUsername ()));
            client.sendUserList (clients);
        }
    }
    public void updateClientUsername (ClientHandler sender, String oldUsername, String newUsername) throws IOException {
        sender.setUsername (newUsername);
        for (ClientHandler client : clients) {
            if (client == sender) {
                client.sendUserList (clients);
                continue;
            }
            client.sendUserList (clients);
            client.sendServerMessage (String.format ("Пользователь %s сменил username на %s", oldUsername, newUsername));
        }
    }
}
