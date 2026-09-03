with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileViewModel.kt", "r") as f:
    content = f.read()

replacement = """
    private val _userName = MutableStateFlow(prefs.getString("user_name", "أوتاكو") ?: "أوتاكو")
    val userName: StateFlow<String> = _userName

    private val _profileImageUri = MutableStateFlow(prefs.getString("profile_image", "") ?: "")
    val profileImageUri: StateFlow<String> = _profileImageUri

    private val _cacheSize = MutableStateFlow("0 MB")
    val cacheSize: StateFlow<String> = _cacheSize

    init {
        calculateCacheSize()
    }

    private fun calculateCacheSize() {
        val cacheDir = getApplication<Application>().cacheDir
        val size = getDirSize(cacheDir)
        _cacheSize.value = formatSize(size)
    }

    private fun getDirSize(dir: java.io.File): Long {
        var size: Long = 0
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        size += getDirSize(file)
                    } else {
                        size += file.length()
                    }
                }
            }
        }
        return size
    }

    private fun formatSize(size: Long): String {
        if (size <= 0) return "0 Bytes"
        val units = arrayOf("Bytes", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return java.text.DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    fun clearCache() {
        val cacheDir = getApplication<Application>().cacheDir
        deleteDir(cacheDir)
        calculateCacheSize()
    }

    private fun deleteDir(dir: java.io.File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (i in children.indices) {
                    val success = deleteDir(java.io.File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
            }
        }
        return dir?.delete() ?: false
    }

    fun updateUserName(name: String) {
"""

content = content.replace("""    private val _userName = MutableStateFlow(prefs.getString("user_name", "أوتاكو") ?: "أوتاكو")
    val userName: StateFlow<String> = _userName

    private val _profileImageUri = MutableStateFlow(prefs.getString("profile_image", "") ?: "")
    val profileImageUri: StateFlow<String> = _profileImageUri

    fun updateUserName(name: String) {""", replacement)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileViewModel.kt", "w") as f:
    f.write(content)
