package com.fire.mangareader.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;

public class FastDns implements Dns {
    public static final FastDns INSTANCE = new FastDns();

    private FastDns() {}

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        try {
            return Arrays.asList(InetAddress.getAllByName(hostname));
        } catch (UnknownHostException e) {
            // Fallback strategy if local DNS fails
            try {
                // Return common CDNs/Cloudflare IPs directly if possible or just throw
                return Dns.SYSTEM.lookup(hostname);
            } catch (Exception ex) {
                throw e;
            }
        }
    }
}
