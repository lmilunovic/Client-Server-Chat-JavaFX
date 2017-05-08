package com.ladislav;

import com.ladislav.model.ChatClient;

import java.util.Scanner;

/**
 * Created by Ladislav on 5/8/2017.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {

        Scanner sc = new Scanner(System.in);

        ChatClient ladislav = new ChatClient("Ladislav");
        ChatClient mirko = new ChatClient("Mirko");
        ladislav.start();
        Thread.sleep(3000);
        mirko.start();
        Thread.sleep(3000);

        mirko.sendPrivateMessage("Ladislav", "Sinđeliću sinji tiću");


    }
}
