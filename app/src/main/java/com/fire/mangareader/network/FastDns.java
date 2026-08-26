package com.fire.mangareader.network;

import android.util.Log;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Dns;

public class FastDns implements Dns {
    private static final String TAG = "FastDns";
    public static final FastDns INSTANCE = new FastDns();

    private FastDns() {}

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (hostname == null) throw new UnknownHostException("hostname == null");
        try {
            return reorderIPv4First(Dns.SYSTEM.lookup(hostname));
        } catch (UnknownHostException e) {
            Log.w(TAG, "System DNS failed for " + hostname + ", attempting Direct InetAddress lookup...");
            try {
                InetAddress[] allByName = InetAddress.getAllByName(hostname);
                List<InetAddress> list = new ArrayList<>();
                if (allByName != null) {
                    for (InetAddress addr : allByName) {
                        list.add(addr);
                    }
                }
                if (!list.isEmpty()) {
                    return reorderIPv4First(list);
                }
                throw e;
            } catch (Exception ex) {
                throw e;
            }
        }
    }

    private List<InetAddress> reorderIPv4First(List<InetAddress> addresses) {
        if (addresses == null || addresses.isEmpty()) return addresses;
        List<InetAddress> ipv4 = new ArrayList<>();
        List<InetAddress> ipv6 = new ArrayList<>();
        for (InetAddress addr : addresses) {
            if (addr instanceof Inet4Address) {
                ipv4.add(addr);
            } else if (addr instanceof Inet6Address) {
                ipv6.add(addr);
            } else {
                ipv4.add(addr);
            }
        }
        if (!ipv4.isEmpty()) {
            ipv4.addAll(ipv6);
            return ipv4;
        }
        return addresses;
    }
}
