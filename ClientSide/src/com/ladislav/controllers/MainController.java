package com.ladislav.controllers;

import com.ladislav.model.ChatClient;
import com.ladislav.model.Message;
import com.ladislav.model.MessageObserver;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private String lastSelectedClient;

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

    private void sendPrivateMessage() {
        String name = lastSelectedClient;
        String message = messageTextField.getText().trim();
        if (!message.equals("")) {
            return;
        }

        model.sendPrivateMessage(name, message);
        msgSession.appendText(model.getName() + " : " + message + "\n");
        messageTextField.clear();
    }

    //TODO finish me
    private void sendBroadcastMessage() {
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

        if (listView.getSelectionModel().getSelectedItem().equals("BROADCAST")) {
            msgSession.appendText(msg.getMessageBody() + "\n");
        }
        // TODO change color of cell (Cell Factory javaFX)
        // later on: make it blinking

    }


    @Override
    public void newLoginAnnouncement(Message msg) {
        // TODO get sender OR: update whole list with new unmodifiable sorted one !?
        Platform.runLater(() -> {
            listView.getItems().add(msg.getSender());
        });

    }

    @Override
    public void newLogoutAnnouncement(Message msg) {
        //TODO  get sender OR: update whole list with new unmodifiable sorted one !?
        Platform.runLater(() -> listView.getItems().remove(msg.getSender()));
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

    @Override
    public void registerSuccessMessage(Message msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerFailMessage(Message msg) {
        throw new UnsupportedOperationException();
    }


    public void handleLogout(ActionEvent actionEvent) throws IOException {
        model.requestLogout();
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("../resources/login_window.fxml"));
        Parent root = loginLoader.load();
        LoginController loginController = loginLoader.getController();

        model.removeMessageObserver(this);
        model.addMessageObserver(loginController);
        loginController.initialise(model);
        Scene scene = new Scene(root);

        Platform.runLater(() -> {
            stage.hide();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        });
    }

    public void handleCloseItem() {
        model.requestLogout();
        Platform.exit();
        System.exit(0);
    }
}
