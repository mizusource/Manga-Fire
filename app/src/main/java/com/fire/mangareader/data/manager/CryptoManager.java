package com.fire.mangareader.data.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoManager {
    private static final String KEY_ALIAS = "mangafire_secret_key";
    private static final String PREF_LEGACY_KEY = "legacy_secret_key";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    
    private final SharedPreferences prefs;
    private final KeyStore keyStore;

    public CryptoManager(Context context) throws Exception {
        this.prefs = context.getSharedPreferences("crypto_prefs", Context.MODE_PRIVATE);
        this.keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        this.keyStore.load(null);
    }

    public SecretKey getSecretKey() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyStore.Entry entry = keyStore.getEntry(KEY_ALIAS, null);
            if (entry instanceof KeyStore.SecretKeyEntry) {
                return ((KeyStore.SecretKeyEntry) entry).getSecretKey();
            }
            return generateNewKey();
        } else {
            String encodedKey = prefs.getString(PREF_LEGACY_KEY, null);
            if (encodedKey != null) {
                byte[] decodedKey = Base64.decode(encodedKey, Base64.DEFAULT);
                return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            }
            return generateNewKey();
        }
    }

    private SecretKey generateNewKey() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS);
            }
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build();
            keyGenerator.init(keySpec);
            return keyGenerator.generateKey();
        } else {
            byte[] keyBytes = new byte[16];
            new SecureRandom().nextBytes(keyBytes);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            prefs.edit().putString(PREF_LEGACY_KEY, Base64.encodeToString(keyBytes, Base64.DEFAULT)).apply();
            return secretKey;
        }
    }
}
