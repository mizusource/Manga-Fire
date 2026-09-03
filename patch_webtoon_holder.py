import re

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonPageHolder.java", "r") as f:
    content = f.read()

# Replace bind argument
content = content.replace("public void bind(String url, String cookies, String refererUrl)", "public void bind(com.fire.mangareader.domain.model.reader.Page page, String cookies, String refererUrl)")
content = content.replace("currentUrl = url;", "currentUrl = page.getImageUrl() != null ? page.getImageUrl() : page.getUrl();")

# Inject Progress Updating inside the network thread
# Find: `byte[] bytes = response.body().bytes();`
# We'll replace it with a custom byte stream that updates progress.
download_logic_old = """                        byte[] bytes = response.body().bytes();"""
download_logic_new = """                        page.setStatus(com.fire.mangareader.domain.model.reader.Page.DOWNLOAD_IMAGE);
                        java.io.InputStream is = response.body().byteStream();
                        long contentLength = response.body().contentLength();
                        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                        int nRead;
                        byte[] data = new byte[16384];
                        long totalRead = 0;
                        while ((nRead = is.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                            totalRead += nRead;
                            page.update(totalRead, contentLength, false);
                            
                            // Optional: Post progress to UI if we had a dedicated progress text
                            // final int p = page.getProgress();
                            // new Handler(Looper.getMainLooper()).post(() -> {
                            //    // update progress bar if it supports percentage
                            // });
                        }
                        buffer.flush();
                        byte[] bytes = buffer.toByteArray();
                        page.update(totalRead, contentLength, true);
                        page.setStatus(com.fire.mangareader.domain.model.reader.Page.READY);
"""
content = content.replace(download_logic_old, download_logic_new)

# Find where it says errorLayout.setVisibility(View.VISIBLE); and set page status to ERROR
error_old = """                        errorLayout.setVisibility(View.VISIBLE);"""
error_new = """                        page.setStatus(com.fire.mangareader.domain.model.reader.Page.ERROR);
                        errorLayout.setVisibility(View.VISIBLE);"""
content = content.replace(error_old, error_new)

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonPageHolder.java", "w") as f:
    f.write(content)
