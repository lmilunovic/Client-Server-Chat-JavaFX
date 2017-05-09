package com.ladislav.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

// TODO IDEAS:
// Provide some options menu to set server's IP address and port manually

public class ChatClient implements Notifier {

    private BufferedReader in;
    private PrintWriter out;

    private String name;
    // String password;

    private volatile boolean logoutRequested;

    // make thread safe
    private Set<MessageObserver> observers = new HashSet<>();

    public ChatClient(String name) {
        this.name = name;
    }

    public void start() {
        Receiver receiver = new Receiver();
        receiver.start();
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
        // can I stop/interrupt thread from in here instead of boolean
        // and send logout request to server ?
        logoutRequested = true;
    }

    private class Receiver extends Thread {

        @Override
        public void run() {

            try {

                Socket clientSocket = new Socket(ClientProtocols.SERVER_IP, ClientProtocols.PORT);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                login();
                getOnlineMembers();

                while (!logoutRequested) {
                    receiveMessage();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // close/destroy everything on logout
            }
        }

        private void receiveMessage() throws IOException {
            int protocol = Integer.parseInt(in.readLine());
            String sender = in.readLine();
            String message = in.readLine();
            notifyObservers(new Message(protocol, sender, message));
        }

        //boolean ?
        private void login() throws IOException {

            while (true) {
                                                                    //gets request to login from server
                int protocol = Integer.parseInt(in.readLine());
                if (protocol != ClientProtocols.LOGIN_REQUEST) {
                    return;
                }
                                                                    // sends back login request and user name
                out.println(ClientProtocols.LOGIN_REQUEST);
                out.println(name);
                                                                    // server responds if login was successful
                protocol = Integer.parseInt(in.readLine());
                if (protocol == ClientProtocols.LOGIN_SUCCESS) {
                    // TODO notify observers
                    System.out.println(name +
                            "! You logged in succesfully.");
                    break;
                }
            }
        }

        private void getOnlineMembers() throws IOException {
            //add protocol out to server !!!
            int onlineNum = Integer.parseInt(in.readLine());
            for (int i = 0; i < onlineNum; i++) {
                receiveMessage();
            }
        }
    }

    @Override
    public void addMessageObserver(MessageObserver o) {
        if (o == null) {
            throw new NullPointerException();
        }

        observers.add(o);
    }

    @Override
    public void removeMessageObserver(MessageObserver o) {
        if (o == null) {
            throw new NullPointerException();
        }

        observers.remove(o);
    }

    @Override
    public void notifyObservers(Message m) {
        if (m == null) {
            throw new NullPointerException();
        }

        for (MessageObserver observer : observers) {
            observer.newMessageReceived(m);
        }
    }

}

