package com.ladislav.model;

/**
 * Created by Ladislav on 5/8/2017.
 */
public class Message {

    private int protocol;
    private String sender;
    private String messageBody;

    public Message(int protocol, String receiver, String messageBody) {
        this.protocol = protocol;
      //  this.sender = sender;
        this.sender = receiver;
        this.messageBody = messageBody;
    }

    public int getProtocol() {
        return protocol;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
