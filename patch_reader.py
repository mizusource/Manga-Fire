import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Replace the toggle click listener
old_listener_pattern = r'ImageView btnToggleDirection = findViewById\(R\.id\.btnToggleDirection\);.*?btnToggleDirection\.setOnClickListener\(v -> \{.*?\}\);'

new_listener = """ImageView btnToggleDirection = findViewById(R.id.btnToggleDirection);
        btnToggleDirection.setOnClickListener(v -> {
            String[] options = {"عمودي (ويب تون)", "أفقي (من اليسار لليمين)", "أفقي (من اليمين لليسار - مانجا)"};
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("اختر وضع القراءة")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) { // Vertical
                        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        btnToggleDirection.setColorFilter(android.graphics.Color.WHITE);
                        Toast.makeText(this, "وضع القراءة: عمودي", Toast.LENGTH_SHORT).show();
                    } else if (which == 1) { // Horizontal LTR
                        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        btnToggleDirection.setColorFilter(android.graphics.Color.GREEN);
                        Toast.makeText(this, "وضع القراءة: أفقي (LTR)", Toast.LENGTH_SHORT).show();
                    } else if (which == 2) { // Horizontal RTL
                        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true); // true for reverse layout
                        btnToggleDirection.setColorFilter(android.graphics.Color.GREEN);
                        Toast.makeText(this, "وضع القراءة: مانجا يابانية (RTL)", Toast.LENGTH_SHORT).show();
                    }
                    
                    layoutManager.setItemPrefetchEnabled(true);
                    layoutManager.setInitialPrefetchItemCount(5);
                    recyclerView.setLayoutManager(layoutManager);
                    
                    if (which != 0) {
                        recyclerView.setOnFlingListener(null);
                        snapHelper.attachToRecyclerView(recyclerView);
                    } else {
                        recyclerView.setOnFlingListener(null);
                    }

                    if (adapter != null && !tvPageIndicator.getText().toString().isEmpty()) {
                        try {
                            int pos = Integer.parseInt(tvPageIndicator.getText().toString().split("/")[0].trim()) - 1;
                            if (pos >= 0) layoutManager.scrollToPosition(pos);
                        } catch (Exception e) {}
                    }
                })
                .show();
        });"""

content = re.sub(old_listener_pattern, new_listener, content, flags=re.DOTALL)

with open(filepath, 'w') as f:
    f.write(content)
print("ChapterReaderActivity patched.")
