package fxml;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.text.TextFlow;

import java.io.PrintWriter;

public class ChatStageController {

    @FXML
    private TextFlow chatArea;
    @FXML
    private TextArea sendMessageTextArea;
    @FXML
    private TextArea usersArea;
    @FXML
    private Button sendButton;
    @FXML
    private ScrollPane scrollPane;



    public ChatStageController() {
        scrollPane = new ScrollPane();
        this.chatArea = new TextFlow();
        this.sendMessageTextArea = new TextArea();
        this.usersArea = new TextArea();
        this.sendButton = new Button();

    }

    public void initialize(PrintWriter output) {
        this.sendButton.setOnAction(event -> {
            if (this.sendMessageTextArea.getText().isEmpty()) {
                event.consume();
            } else {
                output.println(this.sendMessageTextArea.getText());
                this.sendMessageTextArea.clear();
            }

        });

        this.sendMessageTextArea.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)){
                if (this.sendMessageTextArea.getText().isEmpty()){
                    event.consume();
                    return;
                }
                output.println(this.sendMessageTextArea.getText());
                event.consume();
                this.sendMessageTextArea.clear();
            }
        });

    }


    public void appendUser(String user) {
        this.usersArea.appendText(user + "\n\r");

    }


//    public void sendMessage(String message) {
//        this.chatArea.appendText(message);
//    }


    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public TextFlow getChatArea() {
        return this.chatArea;
    }

    public TextArea getSendMessageTextArea() {
        return this.sendMessageTextArea;
    }

    public TextArea getUsersArea() {
        return this.usersArea;
    }

    @FXML
    public void exit() {
        System.exit(0);
    }
}