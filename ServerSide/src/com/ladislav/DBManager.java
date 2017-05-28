package com.ladislav;

import java.sql.*;
import java.util.List;

/**
 *  Class that provides communication with database (sqlite in this case)
 *  Implements database access object.
 */
public class DBManager implements ChatServerDAO {

    private static final String DB_NAME = "registered_clients.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\Ladislav\\Desktop\\Java\\Chat App\\ServerSide\\src\\com\\ladislav\\" + DB_NAME;
    private static final String TABLE_CLIENT = "chatClients";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";


    /**
     *  Connects to database and calls insertContact method.
     * @param name
     * @param password
     * @param email
     * @return true if insertion was successful.
     */
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

    /**
     * Connects to a database and calls approveLogin.
     * @param name
     * @param password
     * @return true if name and password matched the ones in database.
     */
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

    /**
     *  Executes actual SQL query and returns results of execution.
     *  It works correctly since username is primary key in database.
     *
     * @param statement
     * @param name
     * @param password
     * @return false if ResultSet is empty.
     * @throws SQLException
     */
    private boolean approveLogin(Statement statement, String name, String password) throws SQLException {

        ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_CLIENT +
                " WHERE " + COLUMN_NAME + "=" + "'" + name + "'" +
                " AND " + COLUMN_PASSWORD + "=" + "'" + password + "'");

        return rs.next();
    }

    /**
     * Connects to database and uses selectContact method.
     *
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


    /**
     * Executes actual SQL query to select single contact.
     * @param statement
     * @param name
     * @return returns Client object only if username and password provided match ones in database
     * @throws SQLException
     */
    private Client selectContact(Statement statement, String name) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM" + TABLE_CLIENT + "WHERE" + COLUMN_NAME + "=" + name);

        if (rs.next()) {
            String password = rs.getString(COLUMN_PASSWORD);
            return new Client(name, password);
        }
        return null;
    }

    /**
     *  Executes actual sql query for insertion.
     * @param statement
     * @param name
     * @param password
     * @param email
     * @return true if row count for insert is greater than 0
     * @throws SQLException
     */
    private boolean insertContact(Statement statement, String name, String password, String email) throws SQLException {
        System.out.println("Inserting contact");

        int updated = statement.executeUpdate("INSERT INTO " + TABLE_CLIENT +
                " ( " + COLUMN_NAME + ", " + COLUMN_PASSWORD + ", " + COLUMN_EMAIL + " )" +
                "VALUES ('" + name + "', '" + password + "', '" + email + "')");

        return updated != 0;
    }
}
