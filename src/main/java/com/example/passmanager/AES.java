package com.example.passmanager;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {

    private static final int KEY_LENGTH = 256;
    private static final int ITERATIONS = 65536;

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static SecretKey deriveKey(String password, byte[] salt) throws Exception
    {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(combined);
    }
    public static String decrypt(String encrypted, SecretKey key) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encrypted);


        byte[] iv = new byte[12];
        byte[] ciphertext = new byte[combined.length - 12];

        System.arraycopy(combined, 0, iv, 0, 12);
        System.arraycopy(combined, 12, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext);
    }


}
