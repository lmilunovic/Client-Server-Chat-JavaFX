package com.ladislav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ladislav.ChatAppProtocols.*;

public class ChatServer {

    private Map<String, PrintWriter> clients;

    public ChatServer() {
        clients = new ConcurrentHashMap<>();
    }


    /**
     * Thread that handles client that are logged to the server.
     * It handles clients login, logout, indirect client-client communication, broadcast communication
     */

    private class ClientHandler extends Thread {

        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private boolean logoutRequested;

        private ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {

                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                handleLogin();
                sendOnlineClients();
                listenAndServe();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();

            }
        }

        private void handleLogin() throws IOException {
            while (true) {

                                                                    // sends login request to client and takes response
                out.println(LOGIN_REQUEST);
                int protocol = Integer.parseInt(in.readLine());

                if (protocol != LOGIN_REQUEST) { // think of some other way to return?
                    return;
                }
                name = in.readLine();

                if (name == null) {
                    // TODO refactor - sends message but doesn't use sendMessage
                    out.println(LOGIN_FAILED);
                    out.println(SERVER);
                    out.println("invalid name");
                    // extract to some constant
                    return;
                }
                                                                     // checks if client not online, log in
                if (!clients.containsKey(name)) {
                    clients.put(name, out);
                    sendMessage(name, LOGIN_SUCCESS, SERVER, "Welcome to Chat Server !");
                    sendMessage(ANNOUNCE_LOGIN, name, LOGIN_MESSAGE);
                    System.out.println(name + "logged in");
                    break;
                }
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

            while (!logoutRequested) {
                int protocol = Integer.parseInt(in.readLine());
                String from;
                String receiver;
                String message;

                switch (protocol) {
                    case PRIVATE_MESSAGE:
                        from = in.readLine();
                        receiver = in.readLine();
                        message = in.readLine();
                        boolean msgSent = sendMessage(receiver, protocol, from, message);
                        if (!msgSent) {
                            sendMessage(from, SEND_FAILED, receiver, SEND_MESSAGE_FAILED);
                        }
                        break;
                    case BROADCAST_MESSAGE:
                        from = in.readLine();
                        message = in.readLine();
                        sendMessage(protocol, from, message);
                        break;
                    case LOGOUT_REQUEST:
                        from = in.readLine();
                        sendMessage(ANNOUNCE_LOGOUT, from, LOGOUT_MESSAGE);
                        logoutRequested = true;
                        break;
                    case ANNOUNCE_LOGIN:
                        from = in.readLine();
                        sendMessage(ANNOUNCE_LOGIN, from, LOGIN_MESSAGE);
                        break;
                }
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
