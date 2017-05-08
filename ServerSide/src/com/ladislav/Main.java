package com.ladislav;

import static com.ladislav.ChatAppProtocols.PORT;

/**
 * Created by Ladislav on 5/5/2017.
 */
public class Main {

    public static void main(String[] args) {

        ChatServer server = new ChatServer();
        server.start(PORT);

    }
}
