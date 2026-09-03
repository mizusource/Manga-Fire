package com.fire.mangareader.domain.model.reader

import android.net.Uri

class Page(
    val index: Int,
    var url: String = "",
    var imageUrl: String? = null,
    var uri: Uri? = null
) : ProgressListener {
    var status: Int = QUEUE
        set(value) {
            field = value
            statusCallback?.invoke(this)
        }

    var progress: Int = 0
        set(value) {
            field = value
            statusCallback?.invoke(this)
        }

    @Transient
    var statusCallback: ((Page) -> Unit)? = null

    companion object {
        const val QUEUE = 0
        const val LOAD_PAGE = 1
        const val DOWNLOAD_IMAGE = 2
        const val READY = 3
        const val ERROR = 4
    }

    val number: Int
        get() = index + 1

    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
        progress = if (contentLength > 0) ((100 * bytesRead) / contentLength).toInt() else -1
    }
}
