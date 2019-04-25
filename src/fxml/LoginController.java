package fxml;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.Notifications;
import javafx.util.Duration;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoginController {

    @FXML
    private TextField nickname;
    @FXML
    private TextField address;
    @FXML
    private TextField port;
    @FXML
    private Button logInButton;

    private BufferedReader input;

    private PrintWriter output;

    private Thread read;

    private ChatStageController chatController;

    private Stage secondStage;

    private InetAddress inetAddress;

    private ImageView imageView;

    private String serverName;

    public LoginController() {
        this.nickname = new TextField();
        this.address = new TextField();
        this.port = new TextField();
        this.logInButton = new Button();

    }

    public void initialize() {

        this.logInButton.setOnAction(event -> {

            String name = this.nickname.getText();
            this.serverName = this.address.getText();
            int port = Integer.parseInt(this.port.getText());

            if (name.isEmpty() || this.serverName.isEmpty() || name.equals(" ")) {
                JOptionPane.showMessageDialog(null, "Couldn't connect to server!");
                return;
            }

            try {
                this.inetAddress = InetAddress.getByName(serverName);

                Socket socket = new Socket(this.inetAddress, port);

                this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                this.output = new PrintWriter(socket.getOutputStream(), true);

                this.output.println(name);

                Stage primary = (Stage) this.logInButton.getScene().getWindow();
                primary.hide();


                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("chatStage.fxml"));

                loader.load();

                this.chatController = loader.getController();
                this.chatController.initialize(output);

                Text text = new Text("\n" + "----- Connecting to " + serverName + " on port " + port + "...");
                this.chatController.getChatArea().getChildren().add(text);
                this.chatController.getChatArea().getChildren().add(new Text("\n\r" + "----- Connected to " + socket.getRemoteSocketAddress() + "..."));

                read = new Read();
                read.start();

                Parent parent = loader.getRoot();
                this.secondStage = new Stage();
                this.secondStage.setTitle("Chat window");
                this.secondStage.setScene(new Scene(parent, 762, 449));
                this.secondStage.initStyle(StageStyle.UNIFIED);
                this.secondStage.setResizable(false);
                this.secondStage.setOnHiding(event1 -> System.exit(0));
                this.secondStage.showAndWait();


            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Couldn't connect to server!");
            }

        });
    }

    class Read extends Thread {
        @Override
        public void run() {
            String message;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    message = input.readLine();
                    if (message != null) {
                        if (readMessage(message)) {
                            return;
                        }
                    }
                } catch (IOException ex) {
                    System.err.println("Failed to read incoming message");
                }
            }
        }
    }

    private boolean readMessage(String message) {
        if (message.startsWith("[")) {
            message = message.substring(1, message.length() - 1);
            List<String> ListUser = new ArrayList<>(
                    Arrays.asList(message.split(", "))
            );
            this.chatController.getUsersArea().clear();
            for (String user : ListUser) {
                this.chatController.getUsersArea().appendText("<@>" + user + "\n\r");
            }
        } else {
            try {
                Text nameText = new Text();
                Text text = new Text();
                if (message.contains(":")) {
                    int index = message.indexOf(":");
                    String name = message.substring(0, index);
                    nameText.setText("\n" + name);
                    nameText.setUnderline(true);
                    nameText.setStyle("-fx-font-weight: bold");

                    text.setText(message.substring(index));
                } else {
                    text.setText(("\n" + message));
                }

                chatController.getChatArea().getChildren().addListener(
                        (ListChangeListener<? super Node>) (change) -> {
                            chatController.getChatArea().layout();
                            chatController.getScrollPane().layout();
                            chatController.getScrollPane().setVvalue(1.0f);
                        });

                Platform.runLater(() -> {
                            //ImageView imageView = new ImageView("http://files.softicons.com/download/web-icons/network-and-security-icons-by-artistsvalley/png/16x16/Regular/Friend%20Smiley.png");

                            addEmoticons(nameText, text);
                        }
                );

            } catch (NullPointerException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to login.\nPlease try again!");
                return true;
            }

        }
        return false;
    }

    private void addEmoticons(Text nameText, Text text) {
        if (text.getText().contains("(beer)")) {
            textReplacement(nameText, text, "/emoji/beer.gif", "(beer)", 24);
        } else if (text.getText().contains(":)")) {
            textReplacement(nameText, text, "/emoji/smile.gif", ":)", 16);
        } else if (text.getText().contains(":D")) {
            textReplacement(nameText, text, "/emoji/laugh.gif", ":D", 16);
        } else if (text.getText().contains("(f)")) {
            textReplacement(nameText, text, "/emoji/finger.gif", "(f)", 16);
        } else if (text.getText().contains(":*")) {
            textReplacement(nameText, text, "/emoji/kiss.gif", "(:*)", 16);
        } else {

            chatController.getChatArea().getChildren().addAll(nameText, text);

            if (secondStage.isIconified()) {
                Notifications notify = Notifications.create().title("Message Alert")
                        .text("You have unread message")
                        .hideAfter(Duration.seconds(2))
                        .position(Pos.BOTTOM_RIGHT);
                notify.darkStyle();
                notify.showInformation();

            }

        }
    }

    private void textReplacement(Text nameText, Text text, String s, String s2, int i) {
        this.imageView = new ImageView(s);
        text.setText(text.getText().replace(s2, " "));
        this.imageView.setFitWidth(i);
        this.imageView.setFitHeight(i);
        chatController.getChatArea().getChildren().addAll(nameText, text, imageView);
    }


}



