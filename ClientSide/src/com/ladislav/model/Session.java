package com.ladislav.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class that holds online client and messages received from and sent to him.
 */
class Session {

    private final List<String> messages;
    private boolean newMessage;
    private boolean online;

    public Session() {
        this.messages = new ArrayList<>();
        online = true;
    }

    public boolean newMessageReceived(){
        return newMessage;
    }

    public void newMessageRead(){
        newMessage = false;
    }

   public void changeOnlineStatus(boolean online) {
       this.online = online;
   }

    public boolean isOnline() {
        return online;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    public void addMessage(String msg) {
        messages.add(msg);
        newMessage = true;
    }
}
