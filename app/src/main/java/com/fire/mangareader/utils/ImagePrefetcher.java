package com.fire.mangareader.utils;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ImagePrefetcher {
    
    // Executor with a fixed thread pool to simulate concurrent fetching 
    private static final ExecutorService executor = Executors.newFixedThreadPool(5);
    
    // Semaphore to strictly limit to 5 concurrent image downloads (prevents Out of Memory)
    private static final Semaphore concurrencySemaphore = new Semaphore(5);

    /**
     * Prefetch images silently in the background.
     * Call this when the user opens a chapter to start preloading the next pages.
     */
    public static void prefetchImages(Context context, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        for (String url : imageUrls) {
            executor.submit(() -> {
                try {
                    // Acquire permission before downloading (Wait if 5 are already downloading)
                    concurrencySemaphore.acquire();
                    
                    // Download image only to Disk Cache
                    Glide.with(context.getApplicationContext())
                            .downloadOnly()
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .submit()
                            .get(); // Blocks this specific thread until downloaded
                            
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Release the permission for the next image
                    concurrencySemaphore.release();
                }
            });
        }
    }
}
