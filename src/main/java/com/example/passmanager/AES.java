package com.example.passmanager;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Class used to provide encryption dn decryption to the database and frontend. Creates a standardized way to store cipher text in the database so that we know
 * how to decrypt it later when needed. Decryption only happens when data access is needed and is never stored anywhere.
 *
 */
public class AES {

    private static final int KEY_LENGTH = 256;
    private static final int ITERATIONS = 65536;

    /**
     * Salt is used to slow down brute force attacks for the passwords that do get validated and to decrease pattern recognition.
     * Uses a byte array with the size 16 as that is a good standard strength for salt generation. Could also use 8 byte or 32 byte.
     * We use Secure Random because it goes based of hardware RNG and not a set algorithm like Math.random.
     * We then fill the byte array with random bytes and return it as the salt where every 6 bits is a character through Base64
     *
     * @return a new 16-byte random salt
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Uses master password to convert it to a character array, the salt we generated above, we then use factory with the PBKDF2WithHmacSHA256
     * algorithm to derive the key. We basically hash the password and salt combination the number of iterations and create the key of that result
     *
     * @param password the plaintext password to derive a key from
     * @param salt     the random salt
     * @return a {@link SecretKey} suitable for AES encryption/decryption
     * @throws Exception if the key derivation algorithm is unavailable
     */
    public static SecretKey deriveKey(String password, byte[] salt) throws Exception
    {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        // actually key generation
        return new SecretKeySpec(keyBytes, "AES");
        // we use SecretKeySpec because factory.generateSecret(spec).getEncoded) returns bytes array
        // And we need a SecretKey object as it implements the SecretKey class and uses Polymorphism
    }

    /**
     * Takes the key and text we need to make cipher text, we use cipher to show how are we creating this cipher text
     * We then use a random IV so that the encryption isn't deterministic. We then have a 128 tag length used for authentication
     * to make sure data hasn't been tampered with which is what GCM does. We then initialize the encryption using cipher.init and turning our input into bytes.
     * After we then actually encrypt which gives us the authentication tag and cipher text to where we combine the IV with the cipher text and the tag and
     * store it in the database eventually. We then convert it to a string so we can store it in the database as a string and not bytes
     *
     *
     * @param plaintext the plaintext string to encrypt
     * @param key       the AES {@link SecretKey} to use for encryption
     * @return a Base64-encoded string containing the IV followed by the ciphertext
     * @throws Exception if encryption fails
     */
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
    /**
     * We use Base64 as the encoder so we first have to get our bytes decoded back into bytes, and we know the structure of the cipher text follows
     * that the first 12 bytes is the IV then follows the cipher text and then the GCM tag which is 128 bits. We then take apart the IV
     * by creating a variable 12 bytes long, and then we have the cipher text and GCM tag that can be decrypted by the key now that we know that.
     * We then call the same encryption type as we did in encrypt and initialize it for decrypt mode with the key and tag specification to where
     * we store the decrypted text into a byte array and return it cast as a string so java does its own encoding.
     *
     * @param encrypted the Base64-encoded encrypted string IV, Cipher text and GCM tag
     * @param key       the AES {@link SecretKey} to use for decryption
     * @return the original plaintext string
     * @throws Exception if decryption fails
     */
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
