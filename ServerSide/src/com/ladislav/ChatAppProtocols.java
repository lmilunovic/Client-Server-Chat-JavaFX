package com.ladislav;

/**
 * Created by Ladislav on 5/5/2017.
 * Protocols for chat Server
 */
public class ChatAppProtocols {

    static final int PORT = 12000;

    static final int LOGIN_REQUEST = 1000;
    static final int ANNOUNCE_LOGIN = 1001;
    static final int LOGOUT_REQUEST = 9000;
    static final int ANNOUNCE_LOGOUT = 9001;
    static final int REQUEST_ONLINE_MEMBERS = 1002;

    static final int LOGIN_SUCCESS = 1000;
    static final int LOGIN_FAILED = -1000;

    static final int PRIVATE_MESSAGE = 5000;
    static final int SEND_FAILED = -5000;
    static final int BROADCAST_MESSAGE = 6000;

    public static final String SERVER = "SERVER";

    static final String LOGOUT_MESSAGE = "Client has logged out from server.";
    static final String LOGIN_MESSAGE = "Client has logged in to the server.";
    static final String SEND_MESSAGE_FAILED = "Message failed. Check if client is online and try again.";

}
