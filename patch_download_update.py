import re

filepath = 'app/src/main/java/com/fire/mangareader/domain/usecase/update/DownloadUpdateUseCase.java'
with open(filepath, 'r') as f:
    content = f.read()

old_error = """                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage()));
                    return;
                }"""

new_error = """                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    String responseMessage = connection.getResponseMessage();
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Server returned HTTP " + responseCode + " " + responseMessage));
                    return;
                }"""

content = content.replace(old_error, new_error)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched DownloadUpdateUseCase.java")
