package com.petconnect.util;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test void bcryptHashesUseUniqueSaltsAndVerify() {
        String first = PasswordUtil.hashPassword("correct horse battery staple");
        String second = PasswordUtil.hashPassword("correct horse battery staple");
        assertNotEquals(first, second);
        assertTrue(first.startsWith("$2"));
        assertTrue(PasswordUtil.verifyPassword("correct horse battery staple", first));
        assertFalse(PasswordUtil.verifyPassword("wrong", first));
    }

    @Test void longPasswordsAreNotTruncatedToTheSameBcryptPassword() {
        String prefix = "a".repeat(80);
        String stored = PasswordUtil.hashPassword(prefix + "first");
        assertTrue(PasswordUtil.verifyPassword(prefix + "first", stored));
        assertFalse(PasswordUtil.verifyPassword(prefix + "second", stored));
    }

    @Test void legacySha256HashesRemainVerifiableForLoginMigration() throws Exception {
        byte[] salt = new byte[16];
        for (int i = 0; i < salt.length; i++) salt[i] = (byte) (i + 1);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        byte[] hash = digest.digest("legacy password".getBytes(StandardCharsets.UTF_8));
        String legacy = Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        assertTrue(PasswordUtil.isLegacyHash(legacy));
        assertTrue(PasswordUtil.verifyPassword("legacy password", legacy));
        assertFalse(PasswordUtil.verifyPassword("incorrect", legacy));
        assertFalse(PasswordUtil.verifyPassword("legacy password", "PLACEHOLDER_HASH"));
    }
}
