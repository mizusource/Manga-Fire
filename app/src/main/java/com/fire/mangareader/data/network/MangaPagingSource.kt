package com.fire.mangareader.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fire.mangareader.domain.model.Manga
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MangaPagingSource(
    private val query: String
) : PagingSource<Int, Manga>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Manga> {
        val page = params.key ?: 1
        return try {
            val response = suspendCancellableCoroutine<List<Manga>> { continuation ->
                MangaScraper.searchMangaPaginated(query, page, object : MangaScraper.ScrapingCallback {
                    override fun onSuccess(mangas: MutableList<Manga>?) {
                        if (continuation.isActive) {
                            continuation.resume(mangas ?: emptyList())
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception(errorMessage ?: "Unknown Error"))
                        }
                    }
                })
            }

            val nextKey = if (response.isEmpty()) null else page + 1
            val prevKey = if (page == 1) null else page - 1

            LoadResult.Page(
                data = response,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Manga>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
