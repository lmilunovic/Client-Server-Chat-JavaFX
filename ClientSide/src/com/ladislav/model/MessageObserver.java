package com.ladislav.model;

/**
 * Created by Ladislav on 5/9/2017.
 */


public interface MessageObserver {

    void newPrivateMessageReceived(Message msg);
    void newBroadCastMessageReceived(Message msg);
    void sendMessageFailed(Message msg);
    void newLoginAnnouncement(Message msg);
    void newLogoutAnnouncement(Message msg);
    void loginSuccessMessage(Message msg);
    void loginFailedMessage(Message msg);
    void registerSuccessMessage(Message msg);
    void registerFailMessage(Message msg);

}


