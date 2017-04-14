package com.ladislav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {

        try (Socket clientSocket = new Socket("127.0.0.1", 4000);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))){

            String toServer;
            while ((toServer = stdIn.readLine()) != null) {
                out.println(toServer);
            }
            String fromServer;

            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
