package com.ladislav.model;

/**
 * Created by Ladislav on 5/9/2017.
 */
public interface MessageObserver {
    void newMessageReceived(Message msg);
}

