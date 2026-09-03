#!/bin/bash

# 1. Create Directories
mkdir -p app/src/main/java/com/fire/mangareader/domain/model/parser
mkdir -p app/src/main/java/com/fire/mangareader/domain/model/source
mkdir -p app/src/main/java/com/fire/mangareader/data/parser
mkdir -p app/src/main/java/com/fire/mangareader/data/network/interceptor

# 2. Generate Parser Models
cat << 'INNER_EOF' > app/src/main/java/com/fire/mangareader/domain/model/parser/ParserModels.kt
package com.fire.mangareader.domain.model.parser

import okhttp3.Headers
import okhttp3.OkHttpClient

enum class ExtractorType { CHAPTERS, PAGES }
enum class ResponseFormat { HTML, JSON_WITH_HTML, JSON }
enum class TransformationType { STRIP_PATH, REGEX_REPLACE, SUBDOMAIN_REPLACE }

data class SelectorConfig(
    val css: String,
    val attr: String? = null,
    val transform: String? = null
)

data class UrlTransformation(
    val type: TransformationType,
    val pattern: String,
    val replacement: String? = null
)

data class ExtractorConfig(
    val name: String,
    val type: ExtractorType,
    val root_selector: String,
    val fields: Map<String, String>? = emptyMap(),
    val parameters: List<String>? = emptyList(),
    val selectors: Map<String, SelectorConfig>? = emptyMap(),
    val response_format: ResponseFormat = ResponseFormat.HTML,
    val url_transformations: List<UrlTransformation>? = emptyList()
)

data class IpMapping(
    val subdomain: String,
    val defaultIp: String,
    val overrides: Map<String, String> = emptyMap()
)

data class SourceImage(
    val image_domain: String,
    val image_path: String
)

data class SourceMetadata(
    val domain: String,
    val scheme: String = "https"
)

data class SourceConfig(
    val version: String,
    val source: SourceMetadata,
    val source_image: SourceImage,
    val ajax_endpoint: String,
    val extractors: List<ExtractorConfig>,
    val ip_mapping: IpMapping?,
    val quality_controller: Boolean = false,
    val direct_ip_mode: Boolean = false
)

data class ParserContext(
    val config: SourceConfig,
    val client: OkHttpClient,
    val parameters: Map<String, Any> = emptyMap(),
    val headers: Headers = Headers.Builder().build(),
    val imageQuality: Int = -1
)
INNER_EOF

# 3. Generate Source Models
cat << 'INNER_EOF' > app/src/main/java/com/fire/mangareader/domain/model/source/SourceModels.kt
package com.fire.mangareader.domain.model.source

data class Source(
    val title: String?,
    val altTitle: List<String>? = null,
    val externalSources: List<String>? = null,
    val url: String?,
    val poster: String?,
    val source: String,
    val slug: String?,
    val requiresCaptcha: Boolean = false,
    val chapters: Int? = 0,
    val latestChapterLabel: String? = null,
    val isMain: Boolean? = false
)

data class SourceChapter(
    val url: String?,
    val name: String?
)

data class SourceManga(
    val url: String?,
    val name: String?,
    val poster: String?,
    val latestChapter: String? = null,
    val timestamp: Long? = null
) {
    fun toWaitList(): WaitListItem {
        val slug = name?.lowercase()?.replace(Regex("[^a-z0-9]"), "-")?.replace(Regex("-+"), "-")?.trim('-') ?: ""
        return WaitListItem(
            title = name,
            slug = slug,
            source = null,
            url = url,
            isNew = true,
            foundInLatest = true
        )
    }
}

data class SourceResponse(
    val sourceUrl: String?,
    val sourceChapters: List<SourceChapter>? = null
)

data class GroupedSource(
    var id: Int?,
    val slug: String,
    val sources: List<Source>
) {
    fun toWaitList(): WaitListItem {
        val firstSource = sources.firstOrNull()
        return WaitListItem(
            title = firstSource?.title,
            slug = slug,
            source = sources,
            url = null,
            isNew = false,
            foundInLatest = true
        )
    }
}

data class MatchedGroupedSource(
    val group: GroupedSource,
    val timestamp: Long?
)

data class WaitListItem(
    val title: String?,
    val slug: String?,
    val source: List<Source>?,
    val url: String?,
    val isNew: Boolean?,
    val foundInLatest: Boolean?
)
INNER_EOF

# 4. Generate OkHttp Interceptor (Direct IP / Bypass)
cat << 'INNER_EOF' > app/src/main/java/com/fire/mangareader/data/network/interceptor/DirectIpInterceptor.kt
package com.fire.mangareader.data.network.interceptor

import com.fire.mangareader.domain.model.parser.IpMapping
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor that bypasses DNS by routing directly to a resolved IP address,
 * while preserving the original Host header to bypass Cloudflare/SNI blocks.
 */
class DirectIpInterceptor(private var ipMapping: IpMapping? = null) : Interceptor {
    
    fun updateMapping(mapping: IpMapping?) {
        this.ipMapping = mapping
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        
        ipMapping?.let { mapping ->
            val originalHost = request.url.host
            val overrideIp = mapping.overrides[originalHost] ?: mapping.defaultIp
            
            if (overrideIp.isNotEmpty()) {
                val newUrl = request.url.newBuilder().host(overrideIp).build()
                request = request.newBuilder()
                    .url(newUrl)
                    .header("Host", originalHost) // Preserve Host for the target server
                    .build()
            }
        }
        
        return chain.proceed(request)
    }
}
INNER_EOF

# 5. Generate Dynamic Parser Engine (Jsoup + Config)
cat << 'INNER_EOF' > app/src/main/java/com/fire/mangareader/data/parser/DynamicParserEngine.kt
package com.fire.mangareader.data.parser

import com.fire.mangareader.domain.model.parser.ExtractorConfig
import com.fire.mangareader.domain.model.parser.SelectorConfig
import com.fire.mangareader.domain.model.parser.TransformationType
import com.fire.mangareader.domain.model.parser.UrlTransformation
import com.fire.mangareader.domain.model.source.SourceChapter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Dynamic HTML/JSON scraping engine.
 * Relies on ExtractorConfig provided by the server to parse data without hardcoded selectors.
 */
class DynamicParserEngine {

    fun parseChapters(html: String, config: ExtractorConfig): List<SourceChapter> {
        val chapters = mutableListOf<SourceChapter>()
        val doc: Document = Jsoup.parse(html)
        val rootElements = doc.select(config.root_selector)

        val urlSelector = config.selectors?.get("url")
        val nameSelector = config.selectors?.get("name")

        for (element in rootElements) {
            val url = extractData(element, urlSelector)
            val name = extractData(element, nameSelector)

            if (url != null && name != null) {
                val finalUrl = applyTransformations(url, config.url_transformations)
                chapters.add(SourceChapter(finalUrl, name))
            }
        }
        return chapters
    }

    fun parsePages(html: String, config: ExtractorConfig): List<String> {
        val pages = mutableListOf<String>()
        val doc = Jsoup.parse(html)
        val rootElements = doc.select(config.root_selector)
        val urlSelector = config.selectors?.get("url")

        for (element in rootElements) {
            val url = extractData(element, urlSelector)
            if (url != null) {
                pages.add(applyTransformations(url, config.url_transformations))
            }
        }
        return pages
    }

    private fun extractData(element: Element, config: SelectorConfig?): String? {
        if (config == null) return null
        val targetElement = if (config.css.isNotEmpty()) element.selectFirst(config.css) else element
        targetElement ?: return null

        return if (!config.attr.isNullOrEmpty()) {
            targetElement.attr(config.attr)
        } else {
            targetElement.text()
        }
    }

    private fun applyTransformations(url: String, transformations: List<UrlTransformation>?): String {
        if (transformations.isNullOrEmpty()) return url
        var finalUrl = url
        for (transform in transformations) {
            when (transform.type) {
                TransformationType.STRIP_PATH -> {
                    finalUrl = finalUrl.replace(transform.pattern.toRegex(), "")
                }
                TransformationType.REGEX_REPLACE -> {
                    finalUrl = finalUrl.replace(transform.pattern.toRegex(), transform.replacement ?: "")
                }
                TransformationType.SUBDOMAIN_REPLACE -> {
                    finalUrl = finalUrl.replaceFirst(transform.pattern.toRegex(), transform.replacement ?: "")
                }
            }
        }
        return finalUrl
    }
}
INNER_EOF

# 6. Ensure Jsoup is in build.gradle
if ! grep -q "org.jsoup:jsoup" app/build.gradle; then
    echo "dependencies { implementation 'org.jsoup:jsoup:1.17.2' }" >> app/build.gradle
    echo "Added Jsoup to build.gradle"
fi

