package com.fire.mangareader.util;

import android.app.Activity;
import android.os.Build;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class DisplayUtils {
    public static void optimizeRefreshRate(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            if (window != null && window.getDecorView() != null) {
                Display display = activity.getWindowManager().getDefaultDisplay();
                if (display != null) {
                    Display.Mode[] supportedModes = display.getSupportedModes();
                    if (supportedModes != null && supportedModes.length > 0) {
                        Display.Mode bestMode = supportedModes[0];
                        for (Display.Mode mode : supportedModes) {
                            if (mode.getRefreshRate() > bestMode.getRefreshRate()) {
                                bestMode = mode;
                            }
                        }
                        WindowManager.LayoutParams attributes = window.getAttributes();
                        attributes.preferredDisplayModeId = bestMode.getModeId();
                        window.setAttributes(attributes);
                    }
                }
            }
        }
    }
}
