package chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Server {
    private static final int SERVER_PORT = 8180;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) {

        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        ) {
            while (true) {
                System.out.println("Ожидание подключения...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Соединение установлено!");
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                sendMessageToClient();
                try {
                    while (true) {
                        String message = in.readUTF();

                        if (message.equals("/server-stop")) {
                            System.out.println("Сервер остановлен");
                            System.exit(0);
                        }

                        System.out.println("Клиент: " + message);
                        out.writeUTF(message.toUpperCase ());
                    }

                } catch (SocketException e) {
                    clientSocket.close();
                    System.out.println("Клиент отключился");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void sendMessageToClient() throws NullPointerException{
        Thread s = new Thread(() -> {
            try {
                while (true) {
                    Scanner scanner = new Scanner (System.in);
                    String messageConsole = "Сообщение от админа: " + scanner.next();
                    out.writeUTF(messageConsole);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        s.setDaemon(true);
        s.start();
    }
}
