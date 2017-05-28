package com.ladislav.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Ladislav on 5/27/2017.
 */
public class RegisterDialogController {


    @FXML
    public TextField emailRegTextField;
    @FXML
    public TextField passwordRegTextField;
    @FXML
    public TextField userNameRegTextField;


    public String getEmail() {
        return emailRegTextField.getText().trim();
    }

    public String getPassword() {
        return passwordRegTextField.getText().trim();
    }

    public String getUserName() {
        return userNameRegTextField.getText().trim();
    }

}
