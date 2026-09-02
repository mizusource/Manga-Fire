package com.fire.mangareader.domain.repository;

import com.fire.mangareader.domain.model.Manga;
import com.fire.mangareader.domain.model.SearchRequest;
import java.util.List;

public interface MangaRepository {
    /**
     * Fetch manga details including chapters from a specific URL.
     */
    void getMangaDetails(String url, Callback<Manga> callback);

    /**
     * Search for manga based on a query or advanced filters.
     */
    void searchManga(SearchRequest request, Callback<List<Manga>> callback);

    /**
     * Fetch the latest updated manga list.
     */
    void getLatestUpdates(Callback<List<Manga>> callback);

    /**
     * Fetch the most popular manga list.
     */
    void getPopularManga(Callback<List<Manga>> callback);

    interface Callback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
