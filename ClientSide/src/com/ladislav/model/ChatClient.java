package com.ladislav.model;

import com.ladislav.ClientProtocols;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import static com.ladislav.ClientProtocols.*;

// TODO IDEAS:
// Provide some options menu to set server's IP address and port manually
// Add session class to keep online members and messages from them
// Maybe keep session in file?
// Hold ObservableList of sessions instead of Notifier thing ?

// TODO model reset() method when logging out or reset in finally ?
public class ChatClient implements Notifier {

    private BufferedReader in;
    private PrintWriter out;

    private String name;
    private String password;

    private boolean logoutRequested;

    private Map<String, List<String>> session;

    // make thread safe to be sure?
    private Set<MessageObserver> observers = new HashSet<>();

    public List<String> getClientSession(String clientName) {
        return Collections.unmodifiableList(session.get(clientName));
    }

    public void start(String name, String password) {
        this.name = name;
        this.password = password;
        logoutRequested = false;
        Receiver receiver = new Receiver();
        receiver.start();

    }

    public void sendPrivateMessage(String to, String message) {
        if (out == null) {
            return; // maybe exception later on
        }
        out.println(PRIVATE_MESSAGE);
        out.println(name);
        out.println(to);
        out.println(message);
        session.get(to).add(message);

        //maybe wait here for response and return false if send failed?!
    }

    //TODO implement me
    public void sendBroadcastMessage(String message) {
        if (out == null) {
            return; // maybie exception later on
        }
        out.println(BROADCAST_MESSAGE);
        out.println(name);
        out.println(message);
    }

    public void requestLogout() {
        // can I stop/interrupt thread from in here instead of boolean
        // and send logout request to server ?
        if (name != null) {
            out.println(LOGOUT_REQUEST);
            out.println(name);
        }
    }

    public String getName() {
        return name;
    }

    private class Receiver extends Thread {

        @Override
        public void run() {

            try {

                Socket clientSocket = new Socket(ClientProtocols.SERVER_IP, ClientProtocols.PORT);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                boolean success = login();

                if (success) {
                    System.out.println("Logged in successfully !");
                    getOnlineMembers();
                    while (!logoutRequested) {
                        receiveMessage();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                observers = new HashSet<>();
                System.out.println("Logged out successfully !");
            }
        }


        private boolean login() throws IOException {

            while (true) {
                //gets request to login from server
                int protocol = Integer.parseInt(in.readLine());
                if (protocol != ClientProtocols.LOGIN_REQUEST) {
                    return false;
                }
                // sends back login request and user name
                out.println(ClientProtocols.LOGIN_REQUEST);
                out.println(name);
                // server responds if login was successful
                protocol = Integer.parseInt(in.readLine());
                if (protocol == ClientProtocols.LOGIN_SUCCESS) {
                    String from = in.readLine();
                    String message = in.readLine();

                    // initialising session
                    session = new HashMap<>();
                    ArrayList<String> broadcastMessages = new ArrayList<>();
                    broadcastMessages.add(message);
                    session.put("BROADCAST", broadcastMessages);

                    notifyObservers(new Message(protocol, from, name, message));
                    return true;
                } else {
                    notifyObservers(new Message(protocol, "SERVER", name, "Invalid name/password, try again"));
                    return false;
                }
            }
        }

        private void receiveMessage() throws IOException {

            int protocol = Integer.parseInt(in.readLine());

            if (protocol == LOGOUT_REQUEST) {
                logoutRequested = true;
                return;
            }
            String sender = in.readLine();
            String message = in.readLine();

            String receiver = name;

            System.out.println("Sender: " + sender + "Receiver: " + receiver);

            notifyObservers(new Message(protocol, sender, receiver, message));

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
            System.out.println("message is null !");
            throw new NullPointerException();
        }
        for (MessageObserver observer : observers) {
            switch (m.getProtocol()) {
                case PRIVATE_MESSAGE:
                    session.get(m.getSender()).add(m.getSender() + " : " + m.getMessageBody());
                    observer.newPrivateMessageReceived(m);
                    break;
                case BROADCAST_MESSAGE:
                    session.get("BROADCAST").add(m.getSender() + " : " + m.getMessageBody());
                    observer.newBroadCastMessageReceived(m);
                    break;
                case ANNOUNCE_LOGIN:
                    session.put(m.getSender(), new ArrayList<>());
                    observer.newLoginAnnouncement(m);
                    break;
                case ANNOUNCE_LOGOUT:
                    session.remove(m.getSender());
                    observer.newLogoutAnnouncement(m);
                    break;
                case SEND_FAILED:
                    observer.loginFailedMessage(m);
                    break;
                case LOGIN_FAILED:
                    observer.loginFailedMessage(m);
                    break;
                case LOGIN_SUCCESS:
                    observer.loginSuccessMessage(m);
                    break;
            }
        }
    }

}

