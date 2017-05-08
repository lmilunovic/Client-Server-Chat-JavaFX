package com.ladislav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import static com.ladislav.ClientProtocols.*;

// TODO IDEAS:
// Provide some options menu to set server's IP address and port manually

public class ChatClient {

    private BufferedReader in;
    private PrintWriter out;

    private Receiver receiver;

    private String name;
    // String password;

    private volatile boolean logoutRequested;

    //TODO make some synchronized que (check concurrency package first)
    // Que<String> messages;

    // TODO make me concurrent (or check if there is class in standard lib) !!!
    private Set<String> onlineClients = new HashSet<>();

    public void start() {

        receiver = new Receiver();
        receiver.start();

    }

    //TODO implement me
    public void readMessage(){
        // reads msg and makes queue empty
    }
    //TODO implement me
    public void getOnlineMembers(){
        // get new members if online
        // make some producer/consumer thing here?
    }

    //TODO implement me
    public void sendBroadcastMessage(String message) {

    }
    //TODO implement me
    public void sendPrivateMessage(String to, String message) {

    }

    public void requestLogout() {
        //field made volatile
        // only one thread from controller will change this
        //and inner listener just reads the value
        logoutRequested = true;
    }


    // TODO implement me!
    public String getName() {
        return name;
    }

    // maybie set name in constructor
    public void setName(String name) {
        this.name = name;
    }

    private class Receiver extends Thread {

        @Override
        public void run() {

            try {
                Socket clientSocket = new Socket(SERVER_IP, PORT);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Handles login
                //Right now, it only log ins when the name on server is unique
                // if not, server sends LOGIN_REQUEST again
                while (true) {

                    //gets request to login from server
                    int protocol = Integer.parseInt(in.readLine());
                    if (protocol != LOGIN_REQUEST) {
                        return;
                    }

                    name = getName();

                    // sends login request and user name
                    out.println(LOGIN_REQUEST);
                    out.println(name);

                    // server responds if login was successful
                    protocol = Integer.parseInt(in.readLine());

                    if (protocol == LOGIN_SUCCESS) {
                        break;
                    }
                }

                // gets online clients
                int onlineNum = Integer.parseInt(in.readLine());
                for (int i = 0; i < onlineNum; i++) {
                    onlineClients.add(in.readLine().trim());
                }

                // listens for messages from server
                while (!logoutRequested) {
                    int protocol = Integer.parseInt(in.readLine());

                    switch (protocol) {
                        case PRIVATE_MESSAGE:
                            // get client name
                            // get message
                            break;
                        case BROADCAST_MESSAGE:
                            // get client name
                            // get broadcast msg
                            break;
                        case SEND_FAILED:
                            // get client name
                            // get message
                            break;
                        case ANNOUNCE_LOGIN:
                            // get client name
                            // get message
                            // add to online set
                            break;
                        case ANNOUNCE_LOGOUT:
                            // get client name
                            // get message
                            // remove from online set
                            break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
