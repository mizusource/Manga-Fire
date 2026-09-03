import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

# Replace setPages with Page mapping
old_onSuccess = """                    @Override
                    public void onSuccess(List<String> imageUrls) {
                        progressBar.setVisibility(View.GONE);
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            adapter.setPages(imageUrls, cookies, chapterUrl);
                        } else {
                            Toast.makeText(ChapterReaderActivity.this, "لا توجد صفحات", Toast.LENGTH_SHORT).show();
                        }
                    }"""

new_onSuccess = """                    @Override
                    public void onSuccess(List<String> imageUrls) {
                        progressBar.setVisibility(View.GONE);
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            List<com.fire.mangareader.domain.model.reader.Page> pageList = new java.util.ArrayList<>();
                            for (int i = 0; i < imageUrls.size(); i++) {
                                com.fire.mangareader.domain.model.reader.Page page = new com.fire.mangareader.domain.model.reader.Page(i, imageUrls.get(i), imageUrls.get(i), null);
                                pageList.add(page);
                            }
                            adapter.setPages(pageList, cookies, chapterUrl);
                        } else {
                            Toast.makeText(ChapterReaderActivity.this, "لا توجد صفحات", Toast.LENGTH_SHORT).show();
                        }
                    }"""

content = content.replace(old_onSuccess, new_onSuccess)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(content)
