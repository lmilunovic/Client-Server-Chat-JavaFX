package com.ladislav.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created by Ladislav on 5/28/2017.
 */
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
