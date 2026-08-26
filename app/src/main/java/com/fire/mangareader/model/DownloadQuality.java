package com.fire.mangareader.model;

public enum DownloadQuality {
    HIGH(
        "جودة فائقة الوضوح (الأصلية)",
        "أعلى دقة للقراءة وللتحميل، بدون أي ضغط للصور (لأصحاب الإنترنت السريع)",
        "أصلية HQ",
        "~ 12 - 18 ميجابايت",
        1200,
        90
    ),
    MEDIUM(
        "جودة متوازنة (موصى بها)",
        "وضوح ممتاز وقراءة سريعة مع توفير 60% من باقة النت والمساحة",
        "توفير 60%",
        "~ 4 - 7 ميجابايت",
        750,
        60
    ),
    LOW(
        "جودة فائقة التوفير (اقتصادية)",
        "قراءة سريعة جداً وتوفير 80% من باقة النت (للأجهزة والمساحات الضعيفة)",
        "اقتصادية LQ",
        "~ 1.5 - 3 ميجابايت",
        480,
        40
    );

    private final String titleAr;
    private final String subtitleAr;
    private final String badge;
    private final String estSizeMbPerChapter;
    private final int maxPixelWidth;
    private final int compressionQuality;

    DownloadQuality(String titleAr, String subtitleAr, String badge, String estSizeMbPerChapter, int maxPixelWidth, int compressionQuality) {
        this.titleAr = titleAr;
        this.subtitleAr = subtitleAr;
        this.badge = badge;
        this.estSizeMbPerChapter = estSizeMbPerChapter;
        this.maxPixelWidth = maxPixelWidth;
        this.compressionQuality = compressionQuality;
    }

    public String getArabicName() {
        return titleAr;
    }

    public String getDescription() {
        return subtitleAr;
    }

    public static DownloadQuality fromPreferences(android.content.Context context) {
        android.content.SharedPreferences sp = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String val = sp.getString("download_quality", "MEDIUM");
        try {
            return DownloadQuality.valueOf(val);
        } catch (Exception e) {
            return MEDIUM;
        }
    }

    public String getSubtitleAr() {
        return subtitleAr;
    }

    public String getBadge() {
        return badge;
    }

    public String getEstSizeMbPerChapter() {
        return estSizeMbPerChapter;
    }

    public int getMaxPixelWidth() {
        return maxPixelWidth;
    }

    public int getCompressionQuality() {
        return compressionQuality;
    }
}
