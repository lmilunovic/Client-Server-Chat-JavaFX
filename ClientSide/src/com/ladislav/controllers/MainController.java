package com.ladislav.controllers;

import com.ladislav.model.ChatClient;
import com.ladislav.model.Message;
import com.ladislav.model.MessageObserver;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

//TODO ideas:
// put tab view in center so you can switch between clients you chat with up there
// first tab is global room

public class MainController implements MessageObserver {

    ChatClient model;

    @FXML
    Stage stage;
    @FXML
    ListView<String> listView;
    @FXML
    TextArea msgSession;
    @FXML
    TextField messageTextField;
    @FXML
    Button sendMessageBtn;

    String lastSelectedClient;

    public void initialise(ChatClient model, Stage stage) {
        this.model = model;
        this.stage = stage;

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        listView.getItems().add("BROADCAST"); // TODO find better solution to add broadcast !

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lastSelectedClient = newValue;
            }
            msgSession.clear();
            List<String> messages = model.getClientSession(newValue);
            for (String message : messages) {
                msgSession.appendText(message + "\n");
            }

        });
    }

    public void sendMessageAction(ActionEvent actionEvent) {
        if (listView.getSelectionModel().getSelectedItem().equals("BROADCAST")) {
            sendBroadcastMessage();
        } else {
            sendPrivateMessage();
        }
    }

    public void sendPrivateMessage() {
        String name = lastSelectedClient;
        String message = messageTextField.getText().trim();
        if (!message.equals("")) {
            model.sendPrivateMessage(name, message);
            msgSession.appendText(model.getName() + " : " + message + "\n");
            messageTextField.clear();
        }
    }

    //TODO finish me
    public void sendBroadcastMessage() {
        String message = messageTextField.getText();
        messageTextField.clear();
        model.sendBroadcastMessage(message);
    }

    //TODO finish me
    @Override
    public void newPrivateMessageReceived(Message msg) {

        if (lastSelectedClient != null && lastSelectedClient.equals(msg.getSender())) {
            System.out.println(lastSelectedClient);
            msgSession.appendText(msg.getSender() + " : " + msg.getMessageBody() + "\n");
        }
        // change color of cell (Cell Factory javaFX)
        // later on: make it blinking

    }

    //TODO finish me
    @Override
    public void newBroadCastMessageReceived(Message msg) {

        if (listView.getSelectionModel().getSelectedItem().equals("BROADCAST")){
            msgSession.appendText(msg.getMessageBody() + "\n");
        }
        // change color of cell (Cell Factory javaFX)
        // later on: make it blinking

    }


    @Override
    public void newLoginAnnouncement(Message msg) {
        // get sender OR: update whole list with new unmodifiable sorted one !?
        Platform.runLater(() -> {
            listView.getItems().add(msg.getSender());
        });

    }

    @Override
    public void newLogoutAnnouncement(Message msg) {
        // get sender OR: update whole list with new unmodifiable sorted one !?
        Platform.runLater(() -> {
            listView.getItems().remove(msg.getSender());
        });
    }

    //TODO finish me
    @Override
    public void sendMessageFailed(Message msg) {
        // get sender
    }

    @Override
    public void loginSuccessMessage(Message msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loginFailedMessage(Message msg) {
        throw new UnsupportedOperationException();
    }


    public void handleLogout(ActionEvent actionEvent) throws IOException {
        model.requestLogout();
        System.exit(0);
//        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("../resources/login_window.fxml"));
//        Parent root = mainLoader.load();
//        LoginController loginController = mainLoader.getController();
//
//        model.addMessageObserver(loginController);
//        model.removeMessageObserver(this);
//        loginController.initialise(model);
//        Scene scene = new Scene(root);
//
//        Platform.runLater(() -> {
//            stage.hide();
//            stage.setTitle("Login");
//            stage.setScene(scene);
//            stage.show();
//        });

    }
}
