package com.ladislav.controllers;

import com.ladislav.model.ChatClient;
import com.ladislav.model.InputValidator;
import com.ladislav.model.Message;
import com.ladislav.model.MessageObserver;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;


public class LoginController implements MessageObserver {

    private ChatClient model;

    @FXML
    private Stage stage;
    @FXML
    public Hyperlink registerHyperlink;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Text resultMessage;
    @FXML
    BorderPane loginBorderPane;

    public void initialise(ChatClient model) {
        this.model = model;
    }

    @FXML
    public void handleLoginAction(ActionEvent actionEvent) {

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        String username = usernameField.getText();
        String password = passwordField.getText();

        //TODO validate username and pass

        model.start(username, password);

    }

    private void loadMainWindow() throws IOException {

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("../resources/main_window.fxml"));
        Parent root = mainLoader.load();
        MainController mainController = mainLoader.getController();

        model.addMessageObserver(mainController);
        model.removeMessageObserver(this);
        mainController.initialise(model, stage);
        Scene scene = new Scene(root);

        Platform.runLater(() -> {

            stage.setOnCloseRequest(t -> {
                model.requestLogout();
                Platform.exit();
                System.exit(0);
            });
            stage.hide();
            stage.setTitle("Chat Server");
            stage.setScene(scene);
            stage.show();
        });

    }


    @FXML
    public void loadRegisterDialog() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(loginBorderPane.getScene().getWindow());
        dialog.setTitle("Register");
        dialog.setHeaderText(" Enter username, password and email to register to the server.");
        ImageView image = new ImageView(this.getClass().getResource("../resources/icons/registration.png").toString());
        image.setFitHeight(80);
        image.setFitWidth(80);
        dialog.setGraphic(image);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../resources/register_dialog.fxml"));
        dialog.getDialogPane().setContent(fxmlLoader.load());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            RegisterDialogController dialogController = fxmlLoader.getController();

            String username = dialogController.getUserName();
            String password = dialogController.getPassword();
            String email = dialogController.getEmail();

            if (validateRegistrationParams(username, password, email)) {
                model.register(username, password, email);
            }
        }

    }

    @FXML
    public void loadConfigureDialog() throws IOException {
        Dialog<ButtonType> configDialog = new Dialog<>();
        configDialog.initOwner(loginBorderPane.getScene().getWindow());
        configDialog.setTitle("Configuration");
        configDialog.setHeaderText("Here you can provide server IP and port on which server listens.");
        ImageView image = new ImageView(this.getClass().getResource("../resources/icons/configuration.png").toString());
        image.setFitHeight(80);
        image.setFitWidth(80);
        configDialog.setGraphic(image);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../resources/configuration_dialog.fxml"));
        configDialog.getDialogPane().setContent(fxmlLoader.load());
        configDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        configDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = configDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ConfigurationDialogController dialogController = fxmlLoader.getController();

            String serverIP = dialogController.getServerIP();
            String PORT = dialogController.getServerPORT();
            //TODO validate ip and port
            model.setIPAndPORT(serverIP, PORT);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
        System.exit(0);
    }


    @Override
    public void loginSuccessMessage(Message msg) {
        try {
            loadMainWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loginFailedMessage(Message msg) {
        resultMessage.setText(msg.getMessageBody());
    }

    @Override
    public void registerSuccessMessage(Message msg) {
        resultMessage.setText(msg.getMessageBody());
    }

    @Override
    public void registerFailMessage(Message msg) {
        resultMessage.setText(msg.getMessageBody());
    }

    private boolean validateRegistrationParams(String username, String password, String email) {
        if (!InputValidator.validateUsername(username)) { // EXTRACT USERNAME_PATTERN_MATCH
            resultMessage.setText("Username must start with letter and contain only letters, numbers _ and - signs.");
            return false;
        }
        if (password.length() < 8) {
            resultMessage.setText("Password needs to be at least 8 characters long");
            return false;
        }
        if (!InputValidator.validateEmail(email)) {
            resultMessage.setText("You must enter valid email address");
            return false;
        }
        return true;
    }

    @Override
    public void newPrivateMessageReceived(Message msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void newBroadCastMessageReceived(Message msg) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void sendMessageFailed(Message msg) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void newLoginAnnouncement(Message msg) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void newLogoutAnnouncement(Message msg) {
        throw new UnsupportedOperationException();

    }
}
