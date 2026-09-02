package com.fire.mangareader.data.network.cookie;

import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import okhttp3.Cookie;

public class CookieWrapper {
    public final Cookie cookie;

    public CookieWrapper(Cookie cookie) {
        this.cookie = cookie;
    }

    public String encodeToString() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeUTF(cookie.name());
        objectOutputStream.writeUTF(cookie.value());
        objectOutputStream.writeLong(cookie.expiresAt());
        objectOutputStream.writeUTF(cookie.domain());
        objectOutputStream.writeUTF(cookie.path());
        objectOutputStream.writeBoolean(cookie.secure());
        objectOutputStream.writeBoolean(cookie.httpOnly());
        objectOutputStream.writeBoolean(cookie.hostOnly());
        objectOutputStream.writeBoolean(cookie.persistent());
        objectOutputStream.close();
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);
    }

    public static CookieWrapper decodeFromString(String str) throws Exception {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(str, Base64.NO_WRAP)));
        String name = objectInputStream.readUTF();
        String value = objectInputStream.readUTF();
        long expiresAt = objectInputStream.readLong();
        String domain = objectInputStream.readUTF();
        String path = objectInputStream.readUTF();
        boolean secure = objectInputStream.readBoolean();
        boolean httpOnly = objectInputStream.readBoolean();
        boolean hostOnly = objectInputStream.readBoolean();
        boolean persistent = objectInputStream.readBoolean();
        objectInputStream.close();

        Cookie.Builder builder = new Cookie.Builder()
                .name(name)
                .value(value)
                .expiresAt(expiresAt)
                .path(path);

        if (hostOnly) {
            builder.hostOnlyDomain(domain);
        } else {
            builder.domain(domain);
        }

        if (secure) {
            builder.secure();
        }

        if (httpOnly) {
            builder.httpOnly();
        }

        return new CookieWrapper(builder.build());
    }

    public String getUniqueKey() {
        return (cookie.secure() ? "https" : "http") + "://" + cookie.domain() + cookie.path() + "|" + cookie.name();
    }
}
