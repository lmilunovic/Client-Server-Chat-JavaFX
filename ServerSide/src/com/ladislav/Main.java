package com.ladislav;

import static com.ladislav.ChatAppProtocols.PORT;

/**
 * Created by Ladislav on 5/5/2017.
 */
public class Main {

    private static int port = PORT;
    public static void main(String[] args) {

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        ChatServer server = new ChatServer();
        server.start(port);
        //thread for console

    }

}


