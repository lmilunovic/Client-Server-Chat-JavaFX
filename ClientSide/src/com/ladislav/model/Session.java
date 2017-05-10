package com.ladislav.model;

/**
 * Created by Ladislav on 5/11/2017.
 */
class Session {

    final String clientName;
    private String messages;

    public Session(String clientName, String messages) {
        this.clientName = clientName;
        this.messages = messages;
    }

    public String getClientName() {
        return clientName;
    }

    public String getMessages() {
        return messages;
    }

    public void updateMessages(String msg) {
        messages += "\n" + msg;
    }
}
