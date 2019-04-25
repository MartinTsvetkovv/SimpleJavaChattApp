package entities;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TestClient {

    private int port;

    private InetAddress address;

    public static void main(String[] args) throws IOException {

        new TestClient("localhost", 12344).run();
    }

    public TestClient(String address, int port) {
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.port = port;
    }

    public void run() throws IOException {

        Socket socket = new Socket(this.address, this.port);

        System.out.println("Successfully connected to server ! ");


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter nickname: ");
        String name = reader.readLine();

        PrintStream output = new PrintStream(socket.getOutputStream());

        output.println(name);


        new Thread(new ReceiveHandler(socket.getInputStream())).start();


        System.out.println("Message: ");

        String line;
        while ((line = reader.readLine()) != null) {
            output.println(line);

        }

        reader.close();
        socket.close();
        output.close();

    }


    class ReceiveHandler implements Runnable {

        private InputStream inputFromServer;

        public ReceiveHandler(InputStream input) {
            this.inputFromServer = input;
        }

        @Override
        public void run() {
            Scanner sc = new Scanner(this.inputFromServer);

            while (sc.hasNextLine()) {
                String currentMessage = "";
                currentMessage = sc.nextLine();
                if (currentMessage.startsWith("[")) {

                    currentMessage = currentMessage.substring(1, currentMessage.length() - 1);
                    System.out.println("\n\tUsers List: " + new ArrayList<>(Arrays.asList(currentMessage.split(", "))) + "\n");

                } else {
                    System.out.println(currentMessage);
                }

            }

            sc.close();

        }
    }


}
