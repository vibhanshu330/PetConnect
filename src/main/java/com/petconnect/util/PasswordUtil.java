package com.petconnect.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** BCrypt for new passwords, with verification support for legacy salt:SHA-256 hashes. */
public final class PasswordUtil {
    private static final int BCRYPT_COST = 12;
    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder(BCRYPT_COST);
    private PasswordUtil() { }

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) throw new IllegalArgumentException("Password is required");
        return BCRYPT.encode(bcryptInput(plainPassword));
    }

    public static boolean verifyPassword(String plainPassword, String storedValue) {
        if (plainPassword == null || storedValue == null) return false;
        if (storedValue.startsWith("$2a$") || storedValue.startsWith("$2b$") || storedValue.startsWith("$2y$")) {
            try { return BCRYPT.matches(bcryptInput(plainPassword), storedValue); }
            catch (IllegalArgumentException ignored) { return false; }
        }
        return verifyLegacySha256(plainPassword, storedValue);
    }

    /** True for the former Base64(salt):Base64(SHA-256) encoding. */
    public static boolean isLegacyHash(String storedValue) {
        return storedValue != null && !storedValue.startsWith("$2") && storedValue.contains(":");
    }

    private static boolean verifyLegacySha256(String password, String storedValue) {
        try {
            String[] parts = storedValue.split(":", -1);
            if (parts.length != 2) return false;
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            if (salt.length != 16 || expected.length != 32) return false;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            return MessageDigest.isEqual(expected, digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (IllegalArgumentException | java.security.NoSuchAlgorithmException ignored) {
            return false;
        }
    }

    /* BCrypt consumes at most 72 bytes. Hash first so long passphrases remain
       distinct and can be migrated without truncation or account lockout. */
    private static String bcryptInput(String password) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (java.security.NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", impossible);
        }
    }
}
