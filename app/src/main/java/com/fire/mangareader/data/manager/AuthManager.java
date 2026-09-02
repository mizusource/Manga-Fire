package com.fire.mangareader.data.manager;

import android.content.Context;
import android.util.Base64;
import com.fire.mangareader.data.repository.ConfigRepository;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AuthManager {
    private static final String PREF_JWT_TOKEN = "jwt_token_secure";
    private final ConfigRepository configRepository;
    private final CryptoManager cryptoManager;

    public AuthManager(Context context) throws Exception {
        this.configRepository = new ConfigRepository(context);
        this.cryptoManager = new CryptoManager(context);
    }

    public void saveSecureToken(String plainToken) throws Exception {
        SecretKey secretKey = cryptoManager.getSecretKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(plainToken.getBytes(StandardCharsets.UTF_8));
        
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        
        String encoded = Base64.encodeToString(combined, Base64.DEFAULT);
        configRepository.saveConfig(PREF_JWT_TOKEN, encoded);
    }

    public String getSecureToken() {
        String encoded = configRepository.getConfigSync(PREF_JWT_TOKEN);
        if (encoded == null || encoded.isEmpty()) return null;

        try {
            byte[] combined = Base64.decode(encoded, Base64.DEFAULT);
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];
            
            System.arraycopy(combined, 0, iv, 0, 16);
            System.arraycopy(combined, 16, encrypted, 0, encrypted.length);

            SecretKey secretKey = cryptoManager.getSecretKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                keyStore.deleteEntry("mangafire_secret_key");
            } catch (Exception ignored) {}
            return null;
        }
    }
}
