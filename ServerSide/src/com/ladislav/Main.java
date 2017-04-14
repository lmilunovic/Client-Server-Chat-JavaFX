package com.ladislav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {

        try ( ServerSocket serverSocket = new ServerSocket(4000);
              Socket clientSocket = serverSocket.accept();
              PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
              BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ){

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                out.println("Server response, received:" + inputLine );

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
