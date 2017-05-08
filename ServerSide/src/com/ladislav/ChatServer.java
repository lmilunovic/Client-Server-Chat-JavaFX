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

                //Handles login
                while (true) {

                    // sends login request to client and takes response
                    out.println(LOGIN_REQUEST);
                    int protocol = Integer.parseInt(in.readLine());

                    if (protocol != LOGIN_REQUEST) { // maybie some other way instead of returning
                        return;
                    }


                    name = in.readLine();

                    if (name == null) {
                        out.println(LOGIN_FAILED);
                        out.println("Login was unsuccessful, name passed is null."); // extract to some constant
                        return;
                    }
                    // checks if client with that name already logged
                    if (!clients.containsKey(name)) {
                        clients.put(name, out);
                        out.println(LOGIN_SUCCESS);
                        sendMessage(ANNOUNCE_LOGIN, name, LOGIN_MESSAGE);
                        break;
                    }
                }

                out.println(clients.size());
                for (String client : clients.keySet()) {
                    out.println(client);
                }

                System.out.println(name + " : " + ANNOUNCE_LOGIN); // use Logger ?

                // Provide method to kick user later ?!
                while (!logoutRequested) {

                    int protocol = Integer.parseInt(in.readLine());
                    String from = in.readLine();
                    String receiver = in.readLine();
                    String message = in.readLine();

                    switch (protocol) {
                        case PRIVATE_MESSAGE:
                            boolean msgSent = sendMessage(receiver, protocol, from, message);
                            if (!msgSent) {
                                sendMessage(from, SEND_FAILED, receiver, SEND_MESSAGE_FAILED);
                            }
                            break;
                        case BROADCAST_MESSAGE:
                            sendMessage(protocol, from, message);
                            break;
                        case LOGOUT_REQUEST:
                            sendMessage(ANNOUNCE_LOGOUT, from, LOGOUT_MESSAGE);
                            logoutRequested = true;
                            break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (name != null) {
                    clients.remove(name); // extract logout method ?
                }
                try {
                    System.out.println(name + " : " + LOGOUT_MESSAGE); // use Logger instead ?
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            sendMessage(receiver, protocol, from, message);
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
