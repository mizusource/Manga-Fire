package com.fire.mangareader.network

import android.content.Context
import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object NetworkClient {

    const val DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

    private val cookieStore = ConcurrentHashMap<String, MutableMap<String, Cookie>>()

    val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val host = url.host
            val hostCookies = cookieStore.getOrPut(host) { ConcurrentHashMap() }
            val cookieManager = runCatching { CookieManager.getInstance() }.getOrNull()
            for (cookie in cookies) {
                hostCookies[cookie.name] = cookie
                runCatching {
                    cookieManager?.setCookie(url.toString(), "${cookie.name}=${cookie.value}; Domain=${cookie.domain}; Path=${cookie.path}")
                }
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val host = url.host
            val result = mutableListOf<Cookie>()
            
            // In-memory cookies
            cookieStore[host]?.values?.let { result.addAll(it) }

            // Sync with Android WebView CookieManager
            runCatching {
                val cookieManager = CookieManager.getInstance()
                val cookieString = cookieManager.getCookie(url.toString())
                if (!cookieString.isNullOrBlank()) {
                    val pairs = cookieString.split(";").map { it.trim() }
                    for (pair in pairs) {
                        val parts = pair.split("=", limit = 2)
                        if (parts.size == 2) {
                            val name = parts[0].trim()
                            val value = parts[1].trim()
                            if (result.none { it.name == name }) {
                                result.add(
                                    Cookie.Builder()
                                        .domain(host)
                                        .name(name)
                                        .value(value)
                                        .build()
                                )
                            }
                        }
                    }
                }
            }
            return result
        }
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("User-Agent", DEFAULT_USER_AGENT)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .header("Accept-Language", "ar,en-US;q=0.9,en;q=0.8")
                .header("sec-ch-ua", "\"Chromium\";v=\"128\", \"Not;A=Brand\";v=\"24\", \"Google Chrome\";v=\"128\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")

            // If no referer is provided, derive it from the URL
            if (original.header("Referer") == null) {
                requestBuilder.header("Referer", "${original.url.scheme}://${original.url.host}/")
            }

            val response = chain.proceed(requestBuilder.build())
            response
        }
        .build()

    @Throws(IOException::class)
    fun fetchHtml(url: String, referer: String? = null): Document {
        val requestBuilder = Request.Builder().url(url)
        if (!referer.isNullOrBlank()) {
            requestBuilder.header("Referer", referer)
        }
        val response: Response = okHttpClient.newCall(requestBuilder.build()).execute()
        val responseBody = response.body?.string() ?: ""
        if (response.code in listOf(403, 503) && responseBody.contains("Just a moment", ignoreCase = true)) {
            throw CloudflareBlockedException(url, response.code)
        }
        return Jsoup.parse(responseBody, url)
    }

    class CloudflareBlockedException(val url: String, val statusCode: Int) :
        IOException("Cloudflare verification required for $url (HTTP $statusCode)")
}
