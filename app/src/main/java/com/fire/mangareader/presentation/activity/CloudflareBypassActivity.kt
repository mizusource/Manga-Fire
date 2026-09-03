package com.fire.mangareader.presentation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CloudflareBypassActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val targetUrl = intent.getStringExtra("url") ?: run {
            Toast.makeText(this, "رابط مفقود", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(
                            title = { Text("تخطي الحماية (Cloudflare)") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        CloudflareWebView(targetUrl = targetUrl) {
                            Toast.makeText(this@CloudflareBypassActivity, "تم التخطي بنجاح!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun CloudflareWebView(targetUrl: String, onSuccess: () -> Unit) {
        val coroutineScope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(true) }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }
                        
                        // حفظ الـ User-Agent الجديد في الإعدادات ليتم استخدامه في OkHttp
                        val sharedPrefs = context.getSharedPreferences("app_config_preferences", Context.MODE_PRIVATE)
                        sharedPrefs.edit().putString("cloudflare_user_agent", settings.userAgentString).apply()

                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this, true)

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                // تفعيل مراقب ملفات الارتباط (Cookies)
                                coroutineScope.launch {
                                    while (true) {
                                        val cookies = CookieManager.getInstance().getCookie(targetUrl)
                                        if (cookies != null && cookies.contains("cf_clearance")) {
                                            CookieManager.getInstance().flush()
                                            onSuccess()
                                            break
                                        }
                                        delay(500)
                                    }
                                }
                            }
                        }
                        loadUrl(targetUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
