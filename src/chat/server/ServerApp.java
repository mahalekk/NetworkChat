package chat.server;

import chat.server.Server;

import java.io.IOException;

public class ServerApp {
    private static final int DEFAULT_PORT = 8180;

    public static void main(String[] args) {

        try {
            new Server(DEFAULT_PORT).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
