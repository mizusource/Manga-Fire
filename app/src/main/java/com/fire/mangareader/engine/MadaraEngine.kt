package com.fire.mangareader.engine

import com.fire.mangareader.model.Chapter
import com.fire.mangareader.model.MangaDetails
import com.fire.mangareader.model.MangaItem
import com.fire.mangareader.model.MangaSourceId
import com.fire.mangareader.model.ReaderPage
import com.fire.mangareader.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder

class MadaraEngine(val source: MangaSourceId) {

    private val baseUrl = source.baseUrl

    suspend fun getMangaList(page: Int = 1, order: String = "latest"): List<MangaItem> = withContext(Dispatchers.IO) {
        val url = when {
            page <= 1 -> "$baseUrl/manga/?m_orderby=$order"
            else -> "$baseUrl/manga/page/$page/?m_orderby=$order"
        }
        val doc = NetworkClient.fetchHtml(url)
        parseMangaListFromDoc(doc)
    }

    suspend fun searchManga(query: String, page: Int = 1): List<MangaItem> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = if (page <= 1) {
            "$baseUrl/?s=$encodedQuery&post_type=wp-manga"
        } else {
            "$baseUrl/page/$page/?s=$encodedQuery&post_type=wp-manga"
        }
        val doc = NetworkClient.fetchHtml(url)
        parseMangaListFromDoc(doc)
    }

    private fun parseMangaListFromDoc(doc: Document): List<MangaItem> {
        val items = mutableListOf<MangaItem>()
        val elements = doc.select(".page-item-detail, .c-tabs-item__content, .manga__item, .badge-pos-1")

        for (el in elements) {
            val titleEl = el.selectFirst(".post-title a, .item-title a, h3 a, h5 a") ?: continue
            val title = titleEl.text().trim()
            val url = titleEl.attr("abs:href")
            if (url.isBlank()) continue

            val imgEl = el.selectFirst("img")
            val coverUrl = imgEl?.let {
                it.attr("abs:data-src").ifBlank {
                    it.attr("abs:data-lazy-src").ifBlank {
                        it.attr("abs:src")
                    }
                }
            } ?: ""

            val latestChapter = el.selectFirst(".chapter a, .latest-chap a, .font-meta.chapter a")?.text()?.trim()
            val rating = el.selectFirst(".score, .total_votes, .rating")?.text()?.trim()

            items.add(
                MangaItem(
                    id = url,
                    title = title,
                    url = url,
                    coverUrl = coverUrl,
                    latestChapter = latestChapter,
                    rating = rating,
                    sourceId = source
                )
            )
        }
        return items
    }

    suspend fun getMangaDetails(mangaUrl: String): MangaDetails = withContext(Dispatchers.IO) {
        val doc = NetworkClient.fetchHtml(mangaUrl)
        val title = doc.selectFirst(".post-title h1, .post-title h3, h1")?.text()?.trim() ?: "بدون عنوان"
        val coverEl = doc.selectFirst(".summary_image img")
        val coverUrl = coverEl?.let {
            it.attr("abs:data-src").ifBlank {
                it.attr("abs:data-lazy-src").ifBlank {
                    it.attr("abs:src")
                }
            }
        } ?: ""

        val description = doc.selectFirst(".description-summary .summary__content, .manga-excerpt, .summary__content")?.text()?.trim()
            ?: "لا يوجد وصف متاح."

        val author = doc.selectFirst(".author-content a, .artist-content a")?.text()?.trim()
        val status = doc.selectFirst(".post-status .summary-content, .status-content")?.text()?.trim()

        val genres = doc.select(".genres-content a").map { it.text().trim() }.filter { it.isNotBlank() }

        // Chapters
        var chapters = parseChapters(doc)
        if (chapters.isEmpty()) {
            // Try ajax /ajax/chapters/
            chapters = fetchAjaxChapters(mangaUrl)
        }

        MangaDetails(
            id = mangaUrl,
            title = title,
            url = mangaUrl,
            coverUrl = coverUrl,
            description = description,
            author = author,
            status = status,
            genres = genres,
            chapters = chapters,
            sourceId = source
        )
    }

    private fun parseChapters(doc: Document): List<Chapter> {
        val chapterElements = doc.select("li.wp-manga-chapter, div.wp-manga-chapter")
        val list = mutableListOf<Chapter>()
        for ((index, el) in chapterElements.withIndex()) {
            val link = el.selectFirst("a") ?: continue
            val name = link.text().trim()
            val url = link.attr("abs:href")
            val date = el.selectFirst(".chapter-release-date i, .chapter-release-date a, span.chapter-release-date")?.text()?.trim()
            if (url.isNotBlank()) {
                list.add(
                    Chapter(
                        id = url,
                        name = name,
                        url = url,
                        date = date,
                        number = index.toFloat()
                    )
                )
            }
        }
        return list
    }

    private suspend fun fetchAjaxChapters(mangaUrl: String): List<Chapter> = withContext(Dispatchers.IO) {
        val ajaxUrl = if (mangaUrl.endsWith("/")) "${mangaUrl}ajax/chapters/" else "$mangaUrl/ajax/chapters/"
        try {
            val request = Request.Builder()
                .url(ajaxUrl)
                .post(FormBody.Builder().build())
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Referer", mangaUrl)
                .build()

            val response = NetworkClient.okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""
            if (body.isNotBlank()) {
                val doc = Jsoup.parse(body, ajaxUrl)
                return@withContext parseChapters(doc)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    suspend fun getChapterPages(chapterUrl: String): List<ReaderPage> = withContext(Dispatchers.IO) {
        val doc = NetworkClient.fetchHtml(chapterUrl, referer = baseUrl)
        val pages = mutableListOf<ReaderPage>()

        // 1. Check for Chapter Protector (AES encrypted DRM script)
        val protector = doc.getElementById("chapter-protector-data")
        if (protector != null) {
            val scriptContent = protector.attr("src").ifBlank { protector.html() }
            val decryptedUrls = MadaraCrypto.decryptChapterProtector(scriptContent)
            if (decryptedUrls.isNotEmpty()) {
                decryptedUrls.forEachIndexed { i, url ->
                    pages.add(ReaderPage(index = i + 1, imageUrl = url, referer = chapterUrl))
                }
                return@withContext pages
            }
        }

        // 2. Fallback to standard DOM image elements
        val imgElements = doc.select(
            ".reading-content img, #readerarea img, .page-break img, .ts-main-image img, div[class*=\"reading-content\"] img, .chapter-image img"
        )

        val seen = mutableSetOf<String>()
        for (img in imgElements) {
            val src = img.attr("abs:data-src").ifBlank {
                img.attr("abs:data-lazy-src").ifBlank {
                    img.attr("abs:src")
                }
            }.trim()

            if (src.isNotBlank() &&
                !src.startsWith("data:image") &&
                !src.contains("favicon") &&
                !src.contains("logo") &&
                src.length > 30 &&
                seen.add(src)
            ) {
                pages.add(ReaderPage(index = pages.size + 1, imageUrl = src, referer = chapterUrl))
            }
        }

        pages
    }
}
