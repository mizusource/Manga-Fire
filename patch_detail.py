with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content = f.read()

replacement = """
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(onClick = { viewModel.fetchDetails(mangaId) }) {
                        Text("إعادة المحاولة")
                    }
                    if (error!!.contains("403") || error!!.contains("Cloudflare")) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            val mangaUrl = try {
                                String(android.util.Base64.decode(mangaId, android.util.Base64.URL_SAFE))
                            } catch (e: Exception) {
                                com.fire.mangareader.data.network.MangaScraper.BASE_URL
                            }
                            val safeUrl = if (mangaUrl.startsWith("http")) mangaUrl else com.fire.mangareader.data.network.MangaScraper.BASE_URL
                            com.fire.mangareader.data.network.CloudflareBypassDialog(context, safeUrl, object : com.fire.mangareader.data.network.CloudflareBypassDialog.BypassCallback {
                                override fun onSuccess(cookies: String?, userAgent: String?) {
                                    viewModel.fetchDetails(mangaId)
                                }
                                override fun onFailed() {
                                    // Handle failure if needed
                                }
                            }).show()
                        }) {
                            Text("تخطي حماية Cloudflare")
                        }
                    }
                }
"""

content = content.replace("""                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchDetails(mangaId) }) {
                        Text("إعادة المحاولة")
                    }
                }""", replacement)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content)
