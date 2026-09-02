package com.fire.mangareader.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

public final class NetworkUtils {
    public interface NetworkStateListener {
        void onNetworkChanged(boolean isOnline);
    }

    public static boolean isOnline(Context context) {
        if (context == null) return false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } catch (Exception e) {
            return false;
        }
    }

    public static ConnectivityManager.NetworkCallback registerNetworkCallback(Context context, NetworkStateListener listener) {
        if (context == null || listener == null) return null;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return null;

            ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> listener.onNetworkChanged(true));
                }

                @Override
                public void onLost(Network network) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> listener.onNetworkChanged(false));
                }
            };

            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            cm.registerNetworkCallback(request, callback);
            return callback;
        } catch (Exception e) {
            return null;
        }
    }

    public static void unregisterNetworkCallback(Context context, ConnectivityManager.NetworkCallback callback) {
        if (context == null || callback == null) return;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                cm.unregisterNetworkCallback(callback);
            }
        } catch (Exception ignored) {}
    }
}
