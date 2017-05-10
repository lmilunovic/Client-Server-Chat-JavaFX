package com.ladislav;

/**
 * Created by Ladislav on 5/6/2017.
 */
public class ClientProtocols {

    public  static final int PORT = 12000;
    public static final String SERVER_IP = "127.0.0.1";

    public static final int LOGIN_REQUEST = 1000;
    public static final int ANNOUNCE_LOGIN = 1001;
    public static final int LOGOUT_REQUEST = 9000;
    public static final int ANNOUNCE_LOGOUT = 9001;

    public static final int REQUEST_ONLINE_MEMBERS = 1002;

    public static final int LOGIN_SUCCESS = 1000;
    public static final int LOGIN_FAILED = -1000;

    public static final int PRIVATE_MESSAGE = 5000;
    public static final int SEND_FAILED = -5000;
    public static final int BROADCAST_MESSAGE = 6000;

    public static final String SERVER = "SERVER";

    public static final String LOGOUT_MESSAGE = "Client has logged out from server.";
    public static final String LOGIN_MESSAGE = "Client has logged in to the server.";
    public static final String SEND_MESSAGE_FAILED = "Message failed. Check if client is online and try again.";

}

