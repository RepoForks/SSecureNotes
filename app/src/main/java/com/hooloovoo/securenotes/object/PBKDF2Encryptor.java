package com.hooloovoo.securenotes.object;

import android.util.Log;

import javax.crypto.SecretKey;

/**
 * Created by angelo on 30/12/13.
 */
public class PBKDF2Encryptor extends Encryptor {

    private static final String TAG = "PBKDF2Encryptor";
    @Override
    public SecretKey deriveKey(String password, byte[] salt) {
        return Crypto.deriveKeyPbkdf2(salt, password);
    }

    @Override
    public String encrypt(String plaintext, String password) {
        byte[] salt = Crypto.generateSalt();
        key = deriveKey(password, salt);
        Log.d(TAG, "Generated key: " + getRawKey());

        return Crypto.encrypt(plaintext, key, salt);
    }

    @Override
    public String decrypt(String ciphertext, String password) {
        return Crypto.decryptPbkdf2(ciphertext, password);
    }
}
