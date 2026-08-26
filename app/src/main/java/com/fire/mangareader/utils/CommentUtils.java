package com.fire.mangareader.utils;

import java.util.concurrent.TimeUnit;

public final class CommentUtils {
    public static String getRelativeTime(long timeInMillis) {
        long diff = System.currentTimeMillis() - timeInMillis;
        if (diff < 15000) {
            return "منذ لحظات";
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (seconds < 60) return "منذ لحظات";
        if (minutes == 1) return "منذ دقيقة";
        if (minutes == 2) return "منذ دقيقتين";
        if (minutes >= 3 && minutes < 11) return "منذ " + minutes + " دقائق";
        if (minutes < 60) return "منذ " + minutes + " دقيقة";
        if (hours == 1) return "منذ ساعة";
        if (hours == 2) return "منذ ساعتين";
        if (hours >= 3 && hours < 11) return "منذ " + hours + " ساعات";
        if (hours < 24) return "منذ " + hours + " ساعة";
        if (days == 1) return "منذ يوم";
        if (days == 2) return "منذ يومين";
        if (days >= 3 && days < 7) return "منذ " + days + " أيام";
        if (days < 14) return "منذ أسبوع";
        if (days < 21) return "منذ أسبوعين";
        if (days < 30) return "منذ " + (days / 7) + " أسابيع";
        if (days < 60) return "منذ شهر";
        if (days < 90) return "منذ شهرين";
        if (days < 365) return "منذ " + (days / 30) + " أشهر";
        if (days < 730) return "منذ سنة";
        if (days < 1095) return "منذ سنتين";
        return "منذ " + (days / 365) + " سنوات";
    }
}
