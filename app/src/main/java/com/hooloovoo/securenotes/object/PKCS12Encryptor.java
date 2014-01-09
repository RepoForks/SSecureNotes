package com.hooloovoo.securenotes.object;

import android.util.Log;

import javax.crypto.SecretKey;

/**
 * Created by angelo on 30/12/13.
 */
public class PKCS12Encryptor extends Encryptor {
    private static final String TAG ="PKCS12Encryptor";
    @Override
    public SecretKey deriveKey(String password, byte[] salt) {
        return Crypto.deriveKeyPkcs12(salt, password);
    }

    @Override
    public String encrypt(String plaintext, String password) {
        byte[] salt = Crypto.generateSalt();
        key = deriveKey(password, salt);
        Log.d(TAG, "Generated key: " + getRawKey());

        return Crypto.encryptPkcs12(plaintext, key, salt);
    }

    @Override
    public String decrypt(String ciphertext, String password) {
        return Crypto.decryptPkcs12(ciphertext, password);
    }
}
