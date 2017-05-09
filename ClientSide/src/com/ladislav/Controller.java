package com.ladislav;

import com.ladislav.model.ChatClient;
import com.ladislav.model.Message;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Ladislav on 5/8/2017.
 */
public class Controller {

    //TODO View view;
    BlockingQueue<Message> messageBox;
    Set<String> onlineUsers;
    ChatClient client;


    // TODO onClickListeners for login / logout

    //TODO onClickListener for send message

    //TODO onClickListener for send broadcast

    // TODO consumer for messages to update view

}
