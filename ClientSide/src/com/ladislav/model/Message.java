package com.ladislav.model;

/**
 * Created by Ladislav on 5/8/2017.
 */
public class Message {

    int protocol;
    //String sender;
    String receiver;
    String messageBody;

    public Message(int protocol, String receiver, String messageBody) {
        this.protocol = protocol;
      //  this.sender = sender;
        this.receiver = receiver;
        this.messageBody = messageBody;
    }

    public int getProtocol() {
        return protocol;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
