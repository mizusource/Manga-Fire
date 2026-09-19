package com.fire.mangareader.engine

import android.util.Base64
import org.json.JSONObject
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object MadaraCrypto {

    /**
     * Decrypts the chapter-protector JSON data script tag.
     * The input is either base64 encoded JS in data:text/javascript;base64,... or raw JS:
     *   wpmangaprotectornonce='...';
     *   chapter_data='{"ct":"...","s":"..."}';
     */
    fun decryptChapterProtector(scriptContent: String): List<String> {
        try {
            val content = if (scriptContent.startsWith("data:text/javascript;base64,")) {
                val b64 = scriptContent.substringAfter("data:text/javascript;base64,")
                String(Base64.decode(b64, Base64.DEFAULT), Charsets.UTF_8)
            } else {
                scriptContent
            }

            val nonce = content.substringAfter("wpmangaprotectornonce='").substringBefore("';")
            val rawChapterJson = content.substringAfter("chapter_data='").substringBefore("';").replace("\\/", "/")
            if (nonce.isEmpty() || rawChapterJson.isEmpty()) {
                return emptyList()
            }

            val jsonObject = JSONObject(rawChapterJson)
            val ct = jsonObject.getString("ct")
            val s = jsonObject.getString("s")

            val ciphertext = Base64.decode(ct, Base64.DEFAULT)
            val salt = hexStringToByteArray(s)

            val decrypted = decryptOpenSslAes(salt, ciphertext, nonce)
            // decrypted string is a JSON array string like ["https://...","https://..."]
            val cleaned = decrypted.filterNot { it == '[' || it == ']' || it == '\\' || it == '"' }
            return cleaned.split(",")
                .map { it.trim() }
                .filter { it.startsWith("http://") || it.startsWith("https://") }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    private fun decryptOpenSslAes(salt: ByteArray, ciphertext: ByteArray, password: String): String {
        val (key, iv) = evpBytesToKey(password.toByteArray(Charsets.UTF_8), salt, 32, 16)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        val decryptedBytes = cipher.doFinal(ciphertext)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    private fun evpBytesToKey(
        password: ByteArray,
        salt: ByteArray,
        keyLen: Int,
        ivLen: Int
    ): Pair<ByteArray, ByteArray> {
        val md = MessageDigest.getInstance("MD5")
        var derived = ByteArray(0)
        var block = ByteArray(0)
        while (derived.size < keyLen + ivLen) {
            md.reset()
            md.update(block)
            md.update(password)
            md.update(salt)
            block = md.digest()
            derived += block
        }
        val key = derived.copyOfRange(0, keyLen)
        val iv = derived.copyOfRange(keyLen, keyLen + ivLen)
        return key to iv
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}
