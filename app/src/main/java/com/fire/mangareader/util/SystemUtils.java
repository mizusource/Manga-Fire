package com.fire.mangareader.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class SystemUtils {

    /**
     * للتحقق مما إذا كان الهاتف متصلاً بالإنترنت (متوافق مع كل إصدارات الأندرويد)
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) return false;
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (networkCapabilities == null) return false;
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                   networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                   networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                   networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    /**
     * دالة آمنة لإظهار رسائل التوست (Toast) من أي Thread حتى لو كانت في الخلفية
     */
    public static void safeToast(Context context, String message) {
        if (context == null || message == null) return;
        new Handler(Looper.getMainLooper()).post(() -> 
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * حساب أقصى حجم دقة (Resolution) تستطيع كرت الشاشة الخاصة بالهاتف رسمه (GPU Max Texture Size)
     * لتفادي انهيار التطبيق أو ظهور الشاشة السوداء في المانجا الطويلة جداً (الويب تون).
     */
    public static int getMaxTextureSize() {
        try {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            
            // Initialize
            int[] version = new int[2];
            egl.eglInitialize(display, version);
            
            // Get Configs
            int[] totalConfigs = new int[1];
            egl.eglGetConfigs(display, null, 0, totalConfigs);
            int configCount = totalConfigs[0];
            
            EGLConfig[] configs = new EGLConfig[configCount];
            egl.eglGetConfigs(display, configs, configCount, totalConfigs);
            
            int[] val = new int[1];
            int maxTextureSize = 0;
            for (int i = 0; i < configCount; i++) {
                egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_MAX_PBUFFER_WIDTH, val);
                if (maxTextureSize < val[0]) {
                    maxTextureSize = val[0];
                }
            }
            
            egl.eglTerminate(display);
            
            // Limit fallback to 2048 in worst case
            return Math.max(maxTextureSize, 2048);
        } catch (Exception e) {
            e.printStackTrace();
            return 2048; // Safe fallback
        }
    }
}
