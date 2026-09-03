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
