package com.ladislav.controllers;

import com.ladislav.model.ChatClient;
import com.ladislav.model.Message;
import com.ladislav.model.MessageObserver;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static com.ladislav.ClientProtocols.LOGIN_FAILED;
import static com.ladislav.ClientProtocols.LOGIN_SUCCESS;

/**
 * Created by Ladislav on 5/8/2017.
 */
public class LoginController implements MessageObserver{

    private ChatClient model;
    private Stage stage;

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Text failMessage;

    public void initModel(ChatClient model) {
        this.model = model;
    }

    @Override
    public void newMessageReceived(Message msg){

        if (msg.getProtocol() == LOGIN_SUCCESS) {
            try {
                loadMainWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (msg.getProtocol() == LOGIN_FAILED) {
            failMessage.setText("Login Failed !");
        } else {
            System.out.println(msg.toString());
        }
    }

    private void loadMainWindow() throws IOException {

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("../resources/main_window.fxml"));
        Parent root = mainLoader.load();
        MainController mainController = mainLoader.getController();

        model.addMessageObserver(mainController);
        mainController.initModel(model);
        Scene scene = new Scene(root);

        Platform.runLater(() -> {
            stage.hide();
            stage.setTitle("Chat Server");
            stage.setScene(scene);
            stage.show();
        });

    }

    @FXML
    public void handleLoginAction(ActionEvent actionEvent) {

        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        String username = usernameField.getText();
        String password = passwordField.getText();

        model.start(username, password);

    }
}
