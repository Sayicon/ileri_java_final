package com.tbl324.auth.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    private final PasswordHasher hasher = new PasswordHasher();

    @Test
    void hash_returnsDifferentHashForSamePlaintext() {
        String h1 = hasher.hash("password123");
        String h2 = hasher.hash("password123");
        assertNotEquals(h1, h2);
    }

    @Test
    void verify_correctPassword_returnsTrue() {
        String hash = hasher.hash("secret");
        assertTrue(hasher.verify("secret", hash));
    }

    @Test
    void verify_wrongPassword_returnsFalse() {
        String hash = hasher.hash("secret");
        assertFalse(hasher.verify("wrong", hash));
    }

    @Test
    void hash_producesNonNullNonEmpty() {
        String hash = hasher.hash("test");
        assertNotNull(hash);
        assertFalse(hash.isBlank());
    }
}
