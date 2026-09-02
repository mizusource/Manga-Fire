package com.fire.mangareader.domain.repository;

import com.fire.mangareader.domain.model.remote.Update;
import java.util.List;

public interface UpdateRepository {
    /**
     * Check for OTA (Over-The-Air) app updates.
     */
    void checkForAppUpdate(Callback<Update> callback);

    /**
     * Check for new chapter updates for a list of favorite mangas.
     */
    void checkForNewChapters(List<String> favoriteMangaUrls, Callback<Boolean> callback);

    interface Callback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
