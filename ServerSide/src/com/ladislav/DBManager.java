package com.ladislav;

import java.sql.*;
import java.util.List;

/**
 * Created by Ladislav on 5/26/2017.
 */
public class DBManager implements serverDAO {

    private static final String DB_NAME = "registered_clients.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\Ladislav\\Desktop\\Java\\Chat App\\ServerSide\\src\\com\\ladislav\\" + DB_NAME;
    private static final String TABLE_CLIENT = "chatClients";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";


    @Override
    public boolean registerClient(String name, String password, String email) {

        boolean registered = false;

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             Statement statement = conn.createStatement()) {
            System.out.println("Connecting to DB and inserting client");
            registered = insertContact(statement, name, password, email);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registered;
    }


    public boolean clientLogin(String name, String password) {
        boolean loginApproved = false;

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             Statement statement = conn.createStatement()) {
            loginApproved = approveLogin(statement, name, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loginApproved;
    }

    // This works correctly cause client name is set as primary key in db
    private boolean approveLogin(Statement statement, String name, String password) throws SQLException {

        ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_CLIENT +
                " WHERE " + COLUMN_NAME + "=" + "'" + name + "'" +
                " AND " + COLUMN_PASSWORD + "=" + "'" + password + "'");

        if (rs.next()) {
            return true;
        }
        return false;
    }

    /**
     * @param name String
     * @return Client object if client with that name exists, and null if not.
     */
    @Override
    public Client getChatClient(String name) {
        Client client = null;

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             Statement statement = conn.createStatement()) {
            client = selectContact(statement, name);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return client;
    }


    @Override
    public List<String> getAllRegisteredMembers() {
        return null;
    }


    private Client selectContact(Statement statement, String name) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM" + TABLE_CLIENT + "WHERE" + COLUMN_NAME + "=" + name);

        if (rs.next()) {
            String password = rs.getString(COLUMN_PASSWORD);
            return new Client(name, password);
        }
        return null;
    }

    private boolean insertContact(Statement statement, String name, String password, String email) throws SQLException {
        System.out.println("Inserting contact");

        int updated = statement.executeUpdate("INSERT INTO " + TABLE_CLIENT +
                " ( " + COLUMN_NAME + ", " + COLUMN_PASSWORD + ", " + COLUMN_EMAIL + " )" +
                "VALUES ('" + name + "', '" + password + "', '" + email + "')");

        return updated != 0;
    }
}
