package com.fire.mangareader.utils

object MangaExtensions {
    
    @JvmStatic
    fun fixImageUrl(url: String?, domain: String? = null): String {
        if (url.isNullOrEmpty()) return ""
        
        if (url.startsWith("https://") || url.startsWith("http://")) return url
        if (url.startsWith("//")) return "https:$url"
        
        if (url.startsWith("wp-content") && domain != null) {
            val base = if (domain.startsWith("http")) domain else "https://$domain"
            return "$base/$url"
        } else if (url.startsWith("wp-content")) {
            return "https://io.lek-manga.net/$url"
        }
        
        if (domain != null) {
            val base = if (domain.startsWith("http")) domain else "https://$domain"
            return if (url.startsWith("/")) {
                "$base$url"
            } else {
                "$base/$url"
            }
        }
        return url
    }

    @JvmStatic
    fun getMangaType(countryCode: String?): String {
        return when (countryCode?.uppercase()) {
            "JP" -> "مانجا يابانية"
            "KR" -> "مانهوا"
            "CN" -> "مانها صينية"
            "AR" -> "عربية"
            else -> "غير محدد"
        }
    }

    @JvmStatic
    fun getMangaFormat(format: String?): String {
        return when (format?.uppercase()) {
            "MANGA" -> "مانغا"
            "NOVEL" -> "رواية"
            "LIGHT_NOVEL" -> "رواية خفيفة"
            "ONE_SHOT" -> "فصل واحد"
            "DOUJINSHI" -> "دوجينشي"
            "MANHWA" -> "مانهوا"
            "MANHUA" -> "مانها"
            else -> "أخرى"
        }
    }

    @JvmStatic
    fun getMangaStatus(status: String?): String {
        return when (status?.uppercase()) {
            "FINISHED", "COMPLETED" -> "منتهية"
            "RELEASING", "ONGOING" -> "مستمرة"
            "NOT_YET_RELEASED" -> "لم يتم إصداره بعد"
            "CANCELLED" -> "ألغيت"
            "HIATUS" -> "متوقف مؤقتا"
            else -> status ?: "غير معروف"
        }
    }
}
