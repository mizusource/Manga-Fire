with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/home/HomeScreen.kt", "r") as f:
    content = f.read()

replacement = """
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Button(onClick = { viewModel.fetchData() }) {
                            Text("إعادة المحاولة")
                        }
                        if (error!!.contains("403") || error!!.contains("Cloudflare")) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                com.fire.mangareader.data.network.CloudflareBypassDialog(context, com.fire.mangareader.data.network.MangaScraper.BASE_URL, object : com.fire.mangareader.data.network.CloudflareBypassDialog.BypassCallback {
                                    override fun onSuccess(cookies: String?, userAgent: String?) {
                                        viewModel.fetchData()
                                    }
                                    override fun onFailed() {}
                                }).show()
                            }) {
                                Text("تخطي حماية Cloudflare")
                            }
                        }
                    }
"""

content = content.replace("""                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchData() }) {
                            Text("إعادة المحاولة")
                        }
                    }""", replacement)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/home/HomeScreen.kt", "w") as f:
    f.write(content)
