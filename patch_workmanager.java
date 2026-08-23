        // Start Background Sync for Updates
        androidx.work.PeriodicWorkRequest updateRequest = new androidx.work.PeriodicWorkRequest.Builder(
                com.fire.mangareader.utils.UpdateCheckWorker.class, 12, java.util.concurrent.TimeUnit.HOURS)
                .build();
        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "MangaUpdateCheck",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                updateRequest);
