package com.fire.mangareader.util;

import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String getTimeAgo(long timeInMillis) {
        long now = System.currentTimeMillis();
        long diff = now - timeInMillis;

        if (diff < 0) {
            return "الآن"; // في حال كان الوقت بالمستقبل بالخطأ
        }

        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "منذ لحظات";
        } else if (diff < TimeUnit.HOURS.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            if (minutes == 1) return "منذ دقيقة";
            if (minutes == 2) return "منذ دقيقتين";
            if (minutes >= 3 && minutes <= 10) return "منذ " + minutes + " دقائق";
            return "منذ " + minutes + " دقيقة";
        } else if (diff < TimeUnit.DAYS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            if (hours == 1) return "منذ ساعة";
            if (hours == 2) return "منذ ساعتين";
            if (hours >= 3 && hours <= 10) return "منذ " + hours + " ساعات";
            return "منذ " + hours + " ساعة";
        } else if (diff < TimeUnit.DAYS.toMillis(7)) {
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            if (days == 1) return "منذ يوم";
            if (days == 2) return "منذ يومين";
            if (days >= 3 && days <= 10) return "منذ " + days + " أيام";
            return "منذ " + days + " يوم";
        } else if (diff < TimeUnit.DAYS.toMillis(30)) {
            long weeks = TimeUnit.MILLISECONDS.toDays(diff) / 7;
            if (weeks == 1) return "منذ أسبوع";
            if (weeks == 2) return "منذ أسبوعين";
            if (weeks >= 3 && weeks <= 10) return "منذ " + weeks + " أسابيع";
            return "منذ " + weeks + " أسبوع";
        } else if (diff < TimeUnit.DAYS.toMillis(365)) {
            long months = TimeUnit.MILLISECONDS.toDays(diff) / 30;
            if (months == 1) return "منذ شهر";
            if (months == 2) return "منذ شهرين";
            if (months >= 3 && months <= 10) return "منذ " + months + " أشهر";
            return "منذ " + months + " شهر";
        } else {
            long years = TimeUnit.MILLISECONDS.toDays(diff) / 365;
            if (years == 1) return "منذ سنة";
            if (years == 2) return "منذ سنتين";
            if (years >= 3 && years <= 10) return "منذ " + years + " سنوات";
            return "منذ " + years + " سنة";
        }
    }
}
