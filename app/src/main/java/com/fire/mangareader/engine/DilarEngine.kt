package com.fire.mangareader.engine

import com.fire.mangareader.model.Chapter
import com.fire.mangareader.model.MangaDetails
import com.fire.mangareader.model.MangaItem
import com.fire.mangareader.model.MangaSourceId
import com.fire.mangareader.model.ReaderPage
import com.fire.mangareader.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.nodes.Document
import java.net.URLEncoder

class DilarEngine {

    private val baseUrl = MangaSourceId.DILAR_TUBE.baseUrl

    suspend fun getMangaList(page: Int = 1): List<MangaItem> = withContext(Dispatchers.IO) {
        val url = if (page <= 1) baseUrl else "$baseUrl/page/$page"
        val doc = NetworkClient.fetchHtml(url)
        parseList(doc)
    }

    suspend fun searchManga(query: String): List<MangaItem> = withContext(Dispatchers.IO) {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val url = "$baseUrl/?s=$encoded"
        val doc = NetworkClient.fetchHtml(url)
        parseList(doc)
    }

    private fun parseList(doc: Document): List<MangaItem> {
        val items = mutableListOf<MangaItem>()
        val elements = doc.select(".card, .manga-card, .post-item, article, .anime-card, .series-card")
        for (el in elements) {
            val link = el.selectFirst("a[href*=\"/manga/\"], a[href*=\"/series/\"], a") ?: continue
            val url = link.attr("abs:href")
            val title = el.selectFirst(".title, h3, h2, .card-title, .name")?.text()?.trim() 
                ?: link.attr("title").ifBlank { link.text() }.trim()

            if (url.isBlank() || title.isBlank() || url == baseUrl || url == "$baseUrl/") continue

            val img = el.selectFirst("img")
            val coverUrl = img?.let {
                it.attr("abs:data-src").ifBlank {
                    it.attr("abs:data-lazy-src").ifBlank {
                        it.attr("abs:src")
                    }
                }
            } ?: ""

            val latestChapter = el.selectFirst(".chapter, .badge, .ep-num")?.text()?.trim()

            items.add(
                MangaItem(
                    id = url,
                    title = title,
                    url = url,
                    coverUrl = coverUrl,
                    latestChapter = latestChapter,
                    sourceId = MangaSourceId.DILAR_TUBE
                )
            )
        }
        return items
    }

    suspend fun getMangaDetails(mangaUrl: String): MangaDetails = withContext(Dispatchers.IO) {
        val doc = NetworkClient.fetchHtml(mangaUrl)
        val title = doc.selectFirst("h1, .title, .series-title")?.text()?.trim() ?: "مانجا"
        val img = doc.selectFirst(".poster img, .cover img, .summary_image img, img.img-fluid")
        val coverUrl = img?.let {
            it.attr("abs:data-src").ifBlank { it.attr("abs:src") }
        } ?: ""

        val desc = doc.selectFirst(".description, .synopsis, .summary, p")?.text()?.trim() ?: "لا يوجد وصف متاح."
        val author = doc.selectFirst(".author, .artist")?.text()?.trim()

        val chapters = mutableListOf<Chapter>()
        val chapterLinks = doc.select("a[href*=\"chapter\"], .chapter-list a, .chapters a, li a")
        for ((idx, ch) in chapterLinks.withIndex()) {
            val href = ch.attr("abs:href")
            val name = ch.text().trim()
            if (href.isNotBlank() && (name.contains("فصل") || name.contains("Chapter") || href.contains("chapter"))) {
                chapters.add(
                    Chapter(
                        id = href,
                        name = name.ifBlank { "فصل ${idx + 1}" },
                        url = href,
                        number = idx.toFloat()
                    )
                )
            }
        }

        MangaDetails(
            id = mangaUrl,
            title = title,
            url = mangaUrl,
            coverUrl = coverUrl,
            description = desc,
            author = author,
            chapters = chapters,
            sourceId = MangaSourceId.DILAR_TUBE
        )
    }

    suspend fun getChapterPages(chapterUrl: String): List<ReaderPage> = withContext(Dispatchers.IO) {
        val doc = NetworkClient.fetchHtml(chapterUrl, referer = baseUrl)
        val pages = mutableListOf<ReaderPage>()
        val images = doc.select(".reader img, .chapter-images img, #chapter-container img, .reading-content img, img.page-image")

        val seen = mutableSetOf<String>()
        for (img in images) {
            val src = img.attr("abs:data-src").ifBlank {
                img.attr("abs:data-lazy-src").ifBlank {
                    img.attr("abs:src")
                }
            }.trim()

            if (src.isNotBlank() && !src.startsWith("data:") && seen.add(src)) {
                pages.add(ReaderPage(index = pages.size + 1, imageUrl = src, referer = chapterUrl))
            }
        }
        pages
    }
}
