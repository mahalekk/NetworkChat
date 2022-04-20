package chat.server;

import java.io.File;
import java.io.IOException;

public class ServerApp {
    private static final int DEFAULT_PORT = 8180;

    static File file = new File ("src/chat/resources/chatLog.txt");;

    public static File getFile() {
        return file;
    }

    public static void main(String[] args) {

        try {
            new Server(DEFAULT_PORT).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
