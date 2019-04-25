package entities;

import java.util.Scanner;

class UserHandler implements Runnable {

    private Server server;
    private UserImpl user;

    public UserHandler(Server server, UserImpl user) {
        this.server = server;
        this.user = user;
        this.server.sendToAllUsers();

    }

    @Override
    public void run() {
        String message;

        Scanner sc = new Scanner(this.user.getInputStream());

        while (sc.hasNextLine()) {
            message = sc.nextLine();

            if (message.startsWith("#")) {
                if (message.contains(" ")) {
                    System.out.println("Private message: " + message);
                    int firstSpace = message.indexOf(" ");
                    String privateMessage = message.substring(1, firstSpace);
                    server.sendMessageToUser(message.substring(firstSpace + 1), user, privateMessage);
                }
            } else {
                this.server.sendToAllUsers(message, user);
            }


        }
        this.server.removeUser(user);
        this.server.sendToAllUsers();
        sc.close();
    }
}

