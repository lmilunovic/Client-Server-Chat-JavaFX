package com.ladislav.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class that holds online client and messages received from and sent to him.
 */
class Session {

    private final String clientName;
    private final List<String> messages;

    public Session(String clientName) {
        this.clientName = clientName;
        this.messages = new ArrayList<>();
    }

    public String getClientName() {
        return clientName;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    public void addMessage(String msg) {
        messages.add(msg);
    }
}
