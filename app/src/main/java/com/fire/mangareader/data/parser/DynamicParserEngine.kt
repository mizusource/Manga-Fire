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
