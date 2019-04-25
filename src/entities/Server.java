package entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private int port;
    private List<UserImpl> users;
    private String nickname;
    private boolean isHere;


    public Server(int port) {
        this.port = port;
        this.users = new ArrayList<>();

    }

    public void run() throws IOException {

        ServerSocket serverSocket = new ServerSocket(port) {
            @Override
            protected void finalize() throws Throwable {
                this.close();
            }
        };

        System.out.println("Port " + this.port + " is now open !");

        while (true) {

            Socket client = serverSocket.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            this.nickname = reader.readLine();

            UserImpl newUser = new UserImpl(client, this.nickname);

            isHere = false;

            duplicateUserName(client, newUser);

        }
    }

    private void duplicateUserName(Socket client, UserImpl newUser) {
        if (this.users.size() > 0) {
            for (UserImpl user : this.users) {
                if (user.getName().equals(newUser.getName())) {
                    isHere = true;
                    newUser.getOutputStream().println("=====-----==== Username is already taken. =====-----====\r\n" +
                            "====-----==== Please try again! =====-----====");
                    break;
                }
            }
            if (!isHere) {
                addNewUser(client, newUser);

            }
        } else {
            addNewUser(client, newUser);
        }
    }

    private void addNewUser(Socket client, UserImpl newUser) {
        System.out.println("New User: " + nickname + " Host :" + client.getInetAddress().getHostAddress());

        this.users.add(newUser);

        for (UserImpl user : users) {
            user.getOutputStream().println("====-----==== "  + newUser.getName() +  " is connected " + "=====-----====" +"\n");
        }

        System.out.println(newUser.getIdentifier());

        newUser.getOutputStream().println("====-----==== Welcome "  + newUser.getName()  + " =====-----===="+"\n");


        new Thread(new UserHandler(this, newUser)).start();
    }

    public void sendToAllUsers() {
        for (UserImpl user : users) {
            user.getOutputStream().println(this.users);

        }
    }

    public void sendMessageToUser(String message, UserImpl userSender, String user) {
        boolean isFind = false;
        for (UserImpl client : users) {
            if (client.getName().equals(user) && client.getIdentifier() != userSender.getIdentifier()) {
                isFind = true;
                userSender.getOutputStream().println(userSender.getName() + " -> " + client.getName() + ": " + message);
                client.getOutputStream().println("(Private message) -> " + userSender.getName() + ": " + message);
            }
        }
        if (!isFind) {
            userSender.getOutputStream().println(userSender.getName() + " -> no one !" + ": " + message);
        }

    }


    public void sendToAllUsers(String message, UserImpl userSender) {
        this.users.forEach(u -> u.getOutputStream().println(userSender.getName() + ": " + message));

//        for (UserImpl user1 : users) {
//            user1.getOutputStream().println(userSender.toString() + ": " + message);
//        }
    }

    public void removeUser(UserImpl user) {
        this.users.remove(user);
        for (UserImpl usersLeft : users) {
            usersLeft.getOutputStream().println("=====-----==== " + user + " is disconnected =====-----====");
        }
    }

}
