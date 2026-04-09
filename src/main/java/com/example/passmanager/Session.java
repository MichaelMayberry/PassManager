package com.example.passmanager;

import javax.crypto.SecretKey;

/**
 * Holds the AES key for the entire app for the duration of a user session derived from initial log in
 * The controllers can access the key without passing it through every method call.
 */
public class Session {

    private static SecretKey key;

    /**
     * Stores the session key after a successful login.
     *
     * @param k the derived {@link SecretKey} to store
     */
    public static void setKey(SecretKey k) {
        key = k;
    }

    /**
     * Returns the session's AES key.
     *
     * @return the key set at login, or it's null if it has not been set
     */
    public static SecretKey getKey() {
        return key;
    }
}
