package com.fire.mangareader.data.network;

import java.util.List;
import com.fire.mangareader.domain.model.Chapter;

public interface MangaSource {
    // اسم الموقع (مثلاً: العاشق، مانجا ليك)
    String getSourceName(); 
    
    // الرابط الأساسي
    String getBaseUrl(); 
    
    // دالة لسحب تفاصيل المانجا وقائمة الفصول (تُعطى كود HTML وتعيد الفصول)
    List<Chapter> extractChapters(String html); 
    
    // دالة لسحب صفحات الفصل (تُعطى كود HTML وتعيد روابط الصور)
    List<String> extractPages(String html); 
}
