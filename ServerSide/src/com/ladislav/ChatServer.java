package com.ladislav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.ladislav.ChatAppProtocols.*;

public class ChatServer {

    private ConcurrentMap<String, PrintWriter> clients;

    private DBManager dbManager;

    public ChatServer() {
        clients = new ConcurrentHashMap<>();
        dbManager = new DBManager();
    }

    /**
     * Thread that handles client that are logged to the server.
     * It handles clients getChatClient, logout, indirect client-client communication, broadcast communication
     */

    private class ClientHandler extends Thread {
        private String name;
        private String password;
        private String email;

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        private ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {

                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println(WELCOME_MESSAGE);
                int protocol = Integer.parseInt(in.readLine());
                System.out.println("Protocol");
                if (protocol == REGISTER_REQUEST) {
                    handleRegister();
                } else if (protocol == LOGIN_REQUEST) {
                    boolean loginSuccess = handleLogin();
                    if (loginSuccess) {
                        sendOnlineClients();
                        listenAndServe();
                    }
                } else {
                    throw new ProtocolException("Wrong protocol received: " + protocol +
                            "Expected protocol: " + REGISTER_REQUEST + " or " + LOGIN_REQUEST);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Closing connection");
                closeConnection();
            }
        }

        private boolean handleLogin() throws IOException {

                System.out.println("Logging in");
                name = in.readLine();
                password = in.readLine();

                if (name == null || password == null) {
                    out.println(LOGIN_FAILED);
                    out.println(SERVER);
                    out.println("Invalid input received.");
                    throw new IOException();
                }

                boolean loginSuccess = dbManager.clientLogin(name, password);
                System.out.println("Login success?");
                //maybe send proper message for login failed if client already logged in
                if (loginSuccess && !clients.containsKey(name)) {
                    clients.put(name, out);
                    sendMessage(name, LOGIN_SUCCESS, SERVER, "Welcome to Chat Server !");
                    sendMessage(ANNOUNCE_LOGIN, name, LOGIN_MESSAGE);
                    System.out.println(name + "logged in to the server");
                    return true;
                } else {
                    out.println(LOGIN_FAILED);
                    out.println(SERVER);
                    out.println("Invalid name or password.");
                    return false;
                }

        }

        private void sendOnlineClients() {

            System.out.println("Sending " + (clients.size() - 1) +
                    " online clients to " + name);
            out.println(clients.size() - 1);

            for (String client : clients.keySet()) {
                if (!client.equals(name)) {
                    sendMessage(name, ANNOUNCE_LOGIN, client, LOGIN_MESSAGE);
                }
            }
        }

        private void listenAndServe() throws IOException {

            while (true) {
                int protocol = Integer.parseInt(in.readLine());
                String sender = in.readLine();
                String receiver;
                String message;

                switch (protocol) {
                    case PRIVATE_MESSAGE:
                        receiver = in.readLine();
                        message = in.readLine();
                        boolean msgSent = sendMessage(receiver, protocol, sender, message);
                        if (!msgSent) {
                            sendMessage(sender, SEND_FAILED, receiver, SEND_MESSAGE_FAILED);
                        }
                        break;
                    case BROADCAST_MESSAGE:
                        message = in.readLine();
                        receiver = in.readLine();
                        sendMessage(protocol, sender, message);
                        break;
                    case ANNOUNCE_LOGIN:
                        sendMessage(ANNOUNCE_LOGIN, sender, LOGIN_MESSAGE);
                        break;
                    case LOGOUT_REQUEST:

                        sendMessage(ANNOUNCE_LOGOUT, sender, LOGOUT_MESSAGE);
                        out.println(LOGOUT_REQUEST);
                        return;
                }
            }
        }

        private void handleRegister() throws IOException {
            System.out.println("Handling register");

            name = in.readLine();
            password = in.readLine();
            email = in.readLine();
            if (dbManager.registerClient(name, password, email)) {
                out.println(REGISTER_SUCCESS);
                out.println("You successfully registered to the server.");
            } else {
                out.println(REGISTER_FAILED);
                out.println("Client with that name is already registered.");
            }
        }

        private void closeConnection() {
            if (name != null) {
                clients.remove(name);
            }
            try {
                System.out.println(name + " : " + LOGOUT_MESSAGE); // use Logger instead ?
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends message to receiver's PrintWriter.
     *
     * @param receiver PrintWriter of the client that receives message.
     * @param protocol Protocol number of message.
     * @param from     Name of the sender client.
     * @param message  Actual message to be displayed.
     */

    private boolean sendMessage(String receiver, int protocol, String from, String message) {
        PrintWriter out = clients.get(receiver);

        if (out == null) {
            return false;
        }

        System.out.println("sending message to " + receiver);

        out.println(protocol);
        out.println(from);
        out.println(message);
        return true;
    }

    /**
     * Sends broadcast message to all clients.
     *
     * @param protocol Protocol number of message.
     * @param from     Name of the sender client.
     * @param message  Actual message to be displayed.
     */

    private void sendMessage(int protocol, String from, String message) {
        for (String receiver : clients.keySet()) {
            if (!receiver.equals(from)) {
                sendMessage(receiver, protocol, from, message);
            }
        }
    }

    public void start(int port) {

        try (ServerSocket connectionListener = new ServerSocket(port)) {
            while (true) {
                new ClientHandler(connectionListener.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
