        ImageView btnToggleDirection = findViewById(R.id.btnToggleDirection);
        btnToggleDirection.setOnClickListener(v -> {
            isHorizontalMode = !isHorizontalMode;
            if (isHorizontalMode) {
                layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                layoutManager.setItemPrefetchEnabled(true);
                layoutManager.setInitialPrefetchItemCount(5);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setOnFlingListener(null);
                snapHelper.attachToRecyclerView(recyclerView);
                btnToggleDirection.setColorFilter(Color.GREEN);
                Toast.makeText(this, "وضع القراءة: أفقي (مانجا)", Toast.LENGTH_SHORT).show();
            } else {
                layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                layoutManager.setItemPrefetchEnabled(true);
                layoutManager.setInitialPrefetchItemCount(5);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setOnFlingListener(null);
                btnToggleDirection.setColorFilter(Color.WHITE);
                Toast.makeText(this, "وضع القراءة: عمودي (ويب تون)", Toast.LENGTH_SHORT).show();
            }
            // Scroll to the current page to preserve position
            if (adapter != null) {
                layoutManager.scrollToPosition(tvPageIndicator.getText().toString().isEmpty() ? 0 : Integer.parseInt(tvPageIndicator.getText().toString().split("/")[0].trim()) - 1);
            }
        });
