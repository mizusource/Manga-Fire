        com.bumptech.glide.Glide.with(this).asBitmap().load(mangaCover).into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.Bitmap>() {
            @Override
            public void onResourceReady(android.graphics.Bitmap resource, com.bumptech.glide.request.transition.Transition<? super android.graphics.Bitmap> transition) {
                coverImage.setImageBitmap(resource);
                androidx.palette.graphics.Palette.from(resource).generate(palette -> {
                    if (palette != null) {
                        int defaultColor = android.graphics.Color.parseColor("#121212");
                        int vibrantColor = palette.getVibrantColor(defaultColor);
                        int darkVibrantColor = palette.getDarkVibrantColor(defaultColor);
                        
                        com.google.android.material.appbar.CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
                        if (collapsingToolbar != null) {
                            collapsingToolbar.setContentScrimColor(darkVibrantColor);
                            collapsingToolbar.setStatusBarScrimColor(darkVibrantColor);
                        }
                        
                        com.google.android.material.floatingactionbutton.FloatingActionButton btnRead = findViewById(R.id.btnRead);
                        if (btnRead != null) {
                            btnRead.setBackgroundTintList(android.content.res.ColorStateList.valueOf(vibrantColor));
                        }

                        com.google.android.material.floatingactionbutton.FloatingActionButton fabFavorite = findViewById(R.id.fabFavorite);
                        if (fabFavorite != null) {
                            fabFavorite.setBackgroundTintList(android.content.res.ColorStateList.valueOf(vibrantColor));
                        }
                    }
                });
            }
            @Override
            public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
        });
