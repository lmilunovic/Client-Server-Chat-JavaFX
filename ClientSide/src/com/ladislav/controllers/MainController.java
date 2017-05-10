package com.ladislav.controllers;

import com.ladislav.model.Message;
import com.ladislav.model.MessageObserver;
import com.ladislav.model.Notifier;

/**
 * Created by Ladislav on 5/9/2017.
 */
public class MainController implements MessageObserver{

    Notifier model;

    public void initModel(Notifier model) {
        this.model = model;
    }

    @Override
    public void newMessageReceived(Message msg) {
        // TODO handle messages received
        System.out.println(msg.toString());
    }


    //TODO onClickListener for send message





}
