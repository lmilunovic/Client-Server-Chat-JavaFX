package com.ladislav;

import java.util.List;

/**
 * Created by Ladislav on 5/26/2017.
 */
public interface serverDAO {
    List<String> getAllRegisteredMembers();
    boolean registerClient(String name, String password, String email);
    Client getChatClient(String name);
}
