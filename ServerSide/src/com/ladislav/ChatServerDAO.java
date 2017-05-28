package com.ladislav;

import java.util.List;

/**
 * Simple interface for database access object.
 */
public interface ChatServerDAO {
    List<String> getAllRegisteredMembers();
    boolean registerClient(String name, String password, String email);
    Client getChatClient(String name);
}
