package com.ladislav.model;

/**
 * Created by Ladislav on 5/8/2017.
 */

public class Message {

    private int protocol;
    private String sender;
    private String receiver;
    private String messageBody;


    public Message(int protocol, String sender, String receiver, String messageBody) {
        this.protocol = protocol;
        this.sender = sender;
        this.receiver = receiver;
        this.messageBody = messageBody;
    }

    public String getReceiver() {
        return receiver;
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

    @Override
    public String toString() {
        return protocol + " " + sender + " " + messageBody;
    }
}
