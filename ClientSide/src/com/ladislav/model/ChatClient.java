package com.ladislav.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.*;

import static com.ladislav.model.ClientProtocols.*;

// TODO IDEAS:
// Provide some options menu to set server's IP address and port manually
// Model reset() method when logging out or reset in finally ?

public class ChatClient implements Notifier {

    private BufferedReader in;
    private PrintWriter out;

    private String name;
    private String password;

    private String serverAddress = SERVER_IP;
    private int serverPort = PORT;

    private boolean logoutRequested;

    private Map<String, Session> sessionMap;
    private Set<MessageObserver> observers = new HashSet<>();

    public void start(String name, String password) {

        this.name = name;
        this.password = password;
        logoutRequested = false;
        Receiver receiver = new Receiver();
        receiver.start();

    }

    public void setIPAndPORT(String ip, String port) {
        serverAddress = ip;
        serverPort = Integer.parseInt(port);
    }

    public void register(String name, String password, String email) {
        Thread t = new Thread(() -> {
            try (Socket clientSocket = new Socket(serverAddress, serverPort);
                 PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                int protocol = Integer.parseInt(input.readLine());

                if (protocol != WELCOME_MESSAGE) {
                    throw new ProtocolException("Wrong protocol received: " + protocol +
                            ". Expected protocol: " + WELCOME_MESSAGE);
                }

                output.println(REGISTER_REQUEST);
                output.println(name);
                output.println(password);
                output.println(email);

                protocol = Integer.parseInt(input.readLine());
                String message = input.readLine();
                System.out.println("PROTOCOL received:" + protocol + "REGISTER_SUCCES: " + REGISTER_SUCCESS);
                notifyObservers(new Message(protocol, SERVER, name, message));

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    public void sendPrivateMessage(String receiver, String message) {
        if (out == null) {
            throw new IllegalStateException();
        }
        sendMessage(PRIVATE_MESSAGE, name, receiver, message);
        sessionMap.get(receiver).addMessage(name + " : " + message);

    }

    public void sendBroadcastMessage(String message) {
        if (out == null) {
            throw new IllegalStateException();
        }
        sendMessage(BROADCAST_MESSAGE, name, "BROADCAST", message);
        sessionMap.get("BROADCAST").addMessage(name + " : " + message);
    }

    public void requestLogout() {
        if (name == null) {
            return;
        }
        out.println(LOGOUT_REQUEST);
        out.println(name);
    }

    private void sendMessage(int protocol, String sender, String receiver, String message) {
        out.println(protocol);
        out.println(sender);
        out.println(receiver);
        out.println(message);
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
                observers = new HashSet<>();
                System.out.println("Logged out successfully !");
            }
        }

        private void login() throws IOException {


            //gets welcome message
            int protocol = Integer.parseInt(in.readLine());
            if (protocol != WELCOME_MESSAGE) {
                throw new ProtocolException("Wrong protocol received: " + protocol +
                        ". Expected protocol: " + WELCOME_MESSAGE);
            }

            // sends back login request and user name
            out.println(LOGIN_REQUEST);
            out.println(name);
            out.println(password);

            // server responds if login was successful
            protocol = Integer.parseInt(in.readLine());
            String from = in.readLine();
            String message = in.readLine();

            if (protocol == LOGIN_SUCCESS) {
                System.out.println("Login success");
                //initialising broadcast session
                sessionMap = new HashMap<>();
                Session broadCastSession = new Session();
                broadCastSession.addMessage(message);
                sessionMap.put("BROADCAST", broadCastSession);

                notifyObservers(new Message(protocol, from, name, message));
                System.out.println("Logged in successfully !");


            } else if (protocol == LOGIN_FAILED) {
                notifyObservers(new Message(protocol, from, name, message));

            } else {
                throw new ProtocolException();
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

            notifyObservers(new Message(protocol, sender, receiver, message));

        }

        //FIXME needs better implementation
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
            throw new NullPointerException("Message is null !");
        }
        for (MessageObserver observer : observers) {
            switch (m.getProtocol()) {
                case PRIVATE_MESSAGE:
                    sessionMap.get(m.getSender()).addMessage(m.getSender() + " : " + m.getMessageBody());
                    observer.newPrivateMessageReceived(m);
                    break;
                case BROADCAST_MESSAGE:
                    sessionMap.get("BROADCAST").addMessage(m.getSender() + " : " + m.getMessageBody());
                    observer.newBroadCastMessageReceived(m);
                    break;
                case ANNOUNCE_LOGIN:
                    sessionMap.put(m.getSender(), new Session());
                    observer.newLoginAnnouncement(m);
                    break;
                case ANNOUNCE_LOGOUT:
                    sessionMap.remove(m.getSender());
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
                case REGISTER_SUCCESS:
                    observer.registerSuccessMessage(m);
                    break;
                case REGISTER_FAILED:
                    observer.registerFailMessage(m);
                    break;
            }
        }
    }

    // or deep copy of list !  !  !
    public List<String> getClientSession(String clientName) {
        return Collections.unmodifiableList(sessionMap.get(clientName).getMessages());
    }

    public String getName() {
        return name;
    }

}

