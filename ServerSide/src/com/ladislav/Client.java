package com.ladislav;

/**
 * Created by Ladislav on 5/27/2017.
 */
public class Client {

    private String name;
    private String password;

    public Client(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
