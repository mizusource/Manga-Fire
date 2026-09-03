package com.fire.mangareader.domain.model.reader

interface ProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}
