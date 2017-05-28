package com.ladislav.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ladislav on 5/28/2017.
 */
public class InputValidator {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_USERNAME_REGEX = Pattern.compile("^[A-Za-z]+[A-Za-z_/-0-9]{2,15}");

    //TODO make password validation, server address validation and port

    public static boolean validateUsername(String username) {
        Matcher matcher = VALID_USERNAME_REGEX.matcher(username);
        return matcher.find();
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}

