import entities.Server;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        try {
            new Server(12344).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
