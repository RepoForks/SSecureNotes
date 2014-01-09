package com.hooloovoo.securenotes.object;

import javax.crypto.SecretKey;

/**
 * Created by angelo on 30/12/13.
 */
public abstract class Encryptor {
    SecretKey key;
    public static String password;
    public static int algorithm = -1;

    abstract public SecretKey deriveKey(String passpword, byte[] salt);

    abstract public String encrypt(String plaintext, String password);

    abstract public String decrypt(String ciphertext, String password);

    public String getRawKey() {
        if (key == null) {
            return null;
        }

        return Crypto.toHex(key.getEncoded());
    }
}
