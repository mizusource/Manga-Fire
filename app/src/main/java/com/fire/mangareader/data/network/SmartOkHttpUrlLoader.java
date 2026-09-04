package com.fire.mangareader.data.network;

import androidx.annotation.NonNull;

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;

public class SmartOkHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    private final ModelLoader<GlideUrl, InputStream> specialLoader;
    private final ModelLoader<GlideUrl, InputStream> defaultLoader;

    // Add dilar.tube to the list of domains that require the special client with cookies
    private final List<String> specialDomains = Arrays.asList("dilar.tube", "mangalik.net", "mangaslayers.com");

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private final OkHttpClient specialClient;
        private final OkHttpClient defaultClient;

        public Factory(OkHttpClient specialClient, OkHttpClient defaultClient) {
            this.specialClient = specialClient;
            this.defaultClient = defaultClient;
        }

        @NonNull
        @Override
        public ModelLoader<GlideUrl, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new SmartOkHttpUrlLoader(
                    new OkHttpUrlLoader.Factory(specialClient).build(multiFactory),
                    new OkHttpUrlLoader.Factory(defaultClient).build(multiFactory)
            );
        }

        @Override
        public void teardown() {
            // Do nothing, OkHttpUrlLoader handles its own teardown
        }
    }

    public SmartOkHttpUrlLoader(ModelLoader<GlideUrl, InputStream> specialLoader, ModelLoader<GlideUrl, InputStream> defaultLoader) {
        this.specialLoader = specialLoader;
        this.defaultLoader = defaultLoader;
    }

    @Override
    public LoadData<InputStream> buildLoadData(@NonNull GlideUrl model, int width, int height, @NonNull Options options) {
        String url = model.toStringUrl();
        for (String domain : specialDomains) {
            if (url.contains(domain)) {
                return specialLoader.buildLoadData(model, width, height, options);
            }
        }
        return defaultLoader.buildLoadData(model, width, height, options);
    }

    @Override
    public boolean handles(@NonNull GlideUrl model) {
        return true;
    }
}
