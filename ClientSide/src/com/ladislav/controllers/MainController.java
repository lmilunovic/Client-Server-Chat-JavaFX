package com.ladislav.controllers;

import com.ladislav.model.ChatClient;
import com.ladislav.model.Message;
import com.ladislav.model.MessageObserver;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import static com.ladislav.ClientProtocols.*;

/**
 * Created by Ladislav on 5/9/2017.
 */

//TODO ideas:
    // put tab view in center so you can switch between clients you chat with up there
    // first tab is global room
public class MainController implements MessageObserver{

    ChatClient model;

    @FXML
    ListView listView;


    public void initModel(ChatClient model) {
        this.model = model;
    }

    @Override
    public void newMessageReceived(Message msg) {
        // TODO handle messages received
        System.out.println(msg.toString());

        switch (msg.getProtocol()) {
            case PRIVATE_MESSAGE:
                break;
            case BROADCAST_MESSAGE:
                break;
            case SEND_FAILED:
                break;
            case ANNOUNCE_LOGIN:
                break;
            case ANNOUNCE_LOGOUT:
                break;
        }

    }

    //TODO onClickListener for send message
    public void sendMessage() {
        int protocol = 000;
        String message = "message";

        if (protocol == PRIVATE_MESSAGE) {
            String receiver = "receiver";
            model.sendPrivateMessage(receiver, message);
        } else {
            model.sendBroadcastMessage(message);
        }

    }



}
