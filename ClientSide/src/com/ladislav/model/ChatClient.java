package com.ladislav.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

// TODO IDEAS:
// Provide some options menu to set server's IP address and port manually

public class ChatClient {

    private BufferedReader in;
    private PrintWriter out;
    private Receiver receiver;

    private String name;
    // String password;

    private volatile boolean logoutRequested;

    BlockingQueue<Message> messageBox;

    // TODO BlockingQueue / Set ! ! !
    private Set<String> onlineClients = new HashSet<>();


    public ChatClient(String name, BlockingQueue<Message> messageBox) {
        this.name = name;
        this.messageBox = messageBox;
    }

    public void start() {
        receiver = new Receiver();
        receiver.start();
    }

    //TODO implement me
    public void getOnlineMembers(){
        // get new members if online
        // make some producer/consumer thing here?
    }

    //TODO implement me
    public void sendPrivateMessage(String to, String message) {
        if (out == null) {
            return; // maybie exception later on
        }
        out.println(ClientProtocols.PRIVATE_MESSAGE);
        out.println(name);
        out.println(to);
        out.println(message);
    }

    //TODO implement me
    public void sendBroadcastMessage(String message) {

    }
    public void requestLogout() {
        // can I stop/interrupt thread from in here instead of boolean ?
        logoutRequested = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private class Receiver extends Thread {

        @Override
        public void run() {

            try {
                Socket clientSocket = new Socket(ClientProtocols.SERVER_IP, ClientProtocols.PORT);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                login();

                System.out.println(name + "! You logged in succesfully.");
                System.out.println(name + "! Server is adding online clients you can chat with:");

                // gets online clients one by one
                int onlineNum = Integer.parseInt(in.readLine());
                for (int i = 0; i < onlineNum; i++) {
                    String client = in.readLine().trim();
                    System.out.println(client);
                    onlineClients.add(client);
                }

                // listens for messages from server // TODO extract method / finish me
                System.out.println(name + "! Server is listening for the messages you may receive.");
                while (!logoutRequested) {

                    // if Private, Broadcast or Message Failed:
                    try {
                        messageBox.put(receiveMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Message receiveMessage() throws IOException {
            int protocol = Integer.parseInt(in.readLine());
            String receiver = in.readLine();
            String message = in.readLine();

            return new Message(protocol,receiver,message);
        }

        //boolean ?
        private void login() throws IOException {
            while (true) {

                //gets request to login from server
                int protocol = Integer.parseInt(in.readLine());
                if (protocol != ClientProtocols.LOGIN_REQUEST) {
                    return;
                }

                // sends login request and user name
                out.println(ClientProtocols.LOGIN_REQUEST);
                out.println(name);

                // server responds if login was successful
                protocol = Integer.parseInt(in.readLine());

                if (protocol == ClientProtocols.LOGIN_SUCCESS) {
                    break;
                }
            }

        }
    }

}




//
//                    switch (protocol) {
//                        case ClientProtocols.PRIVATE_MESSAGE:
//                            String sender = in.readLine();
//                            String msg = in.readLine();
//                            System.out.println(name + "! you received a private message from:" + sender);
//                            System.out.println("Message: " + msg);
//                            break;
//                        case ClientProtocols.BROADCAST_MESSAGE:
//                            // get client name
//                            // get broadcast msg
//                            break;
//                        case ClientProtocols.SEND_FAILED:
//                            // get client name
//                            // get message
//                            break;
//                        case ClientProtocols.ANNOUNCE_LOGIN:
//                            sender = in.readLine();
//                            msg = in.readLine();
//                            onlineClients.add(sender);
//                            System.out.println(name + "! "+ sender +" announced to you that he is online");
//                            break;
//                        case ClientProtocols.ANNOUNCE_LOGOUT:
//                            // get client name
//                            // get message
//                            // remove from online set
//                            break;
//                    }
