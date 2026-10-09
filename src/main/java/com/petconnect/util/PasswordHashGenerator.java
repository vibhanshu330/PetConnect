package com.petconnect.util;

import java.io.Console;

/**
 * One-time development utility for generating a password hash for an
 * administrator account. Enter the password at the hidden console prompt.
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        Console console = System.console();
        if (console == null) {
            System.err.println("Run this utility in a terminal so the password can be entered without echo.");
            System.exit(1);
        }
        char[] password = console.readPassword("Password to hash: ");
        if (password == null || password.length == 0) {
            System.err.println("A non-empty password is required.");
            System.exit(1);
        }
        String plainPassword = new String(password);
        String hash = PasswordUtil.hashPassword(plainPassword);
        System.out.println("Password hash: " + hash);
    }
}
