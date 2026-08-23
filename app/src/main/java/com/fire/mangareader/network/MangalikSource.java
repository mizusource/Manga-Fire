package com.fire.mangareader.network;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;
import com.fire.mangareader.model.Chapter;

public class MangalikSource implements MangaSource {

    @Override
    public String getSourceName() { return "MangaLik"; }

    @Override
    public String getBaseUrl() { return "https://mangalik.com"; }

    @Override
    public List<Chapter> extractChapters(String html) {
        List<Chapter> chapters = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements chapterElements = doc.select("li.wp-manga-chapter a, .listing-chapters_wrap a"); // تاجات هذا الموقع تحديداً
        
        for (Element link : chapterElements) {
            Chapter chapter = new Chapter();
            chapter.setUrl(link.absUrl("href"));
            chapter.setTitle(link.text());
            chapters.add(chapter);
        }
        return chapters;
    }

    @Override
    public List<String> extractPages(String html) {
        List<String> pages = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements images = doc.select(".reading-content img, .page-break img");
        
        for (Element img : images) {
            String imgUrl = img.attr("data-src").isEmpty() ? img.attr("src") : img.attr("data-src");
            pages.add(imgUrl.trim());
        }
        return pages;
    }
}
