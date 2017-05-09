package com.ladislav.model;

/**
 * Created by Ladislav on 5/9/2017.
 */
public interface Notifier {
    void addMessageObserver(MessageObserver o);
    void removeMessageObserver(MessageObserver o);
    void notifyObservers(Message m);
}
