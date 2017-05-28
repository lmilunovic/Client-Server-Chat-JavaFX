package com.ladislav.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ConfigurationDialogController {

    @FXML
    TextField serverIPTextField;
    @FXML
    TextField serverPortTextField;

    public String getServerIP() {
        return serverIPTextField.getText().trim();
    }

    public String getServerPORT() {
        return serverPortTextField.getText().trim();
    }

}
