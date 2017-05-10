package com.ladislav.controllers;

import com.ladislav.model.ChatClient;
import com.ladislav.model.Message;
import com.ladislav.model.MessageObserver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static com.ladislav.ClientProtocols.LOGIN_SUCCESS;

/**
 * Created by Ladislav on 5/8/2017.
 */
public class LoginController implements MessageObserver{

    private ChatClient model;
    private Stage stage;
    private Scene scene;

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
                loadMainPanel();
        } else {
            failMessage.setText("Login Failed !");
        }
    }

    public void handleLoginClick(MouseEvent mouseEvent)throws IOException {
        stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("../resources/main_panel.fxml"));
        Parent root = mainLoader.load();
        MainController mainController = mainLoader.getController();

        model.addMessageObserver(mainController);
        mainController.initModel(model);

        scene = new Scene(root);

        String username = usernameField.getText();
        String password = passwordField.getText();

        model.start(username, password);

    }

    private void loadMainPanel()  {
        Platform.runLater(() -> {
            stage.hide();
            stage.setTitle("Chat Server");
            stage.setScene(scene);
            stage.show();
        });

    }

}
