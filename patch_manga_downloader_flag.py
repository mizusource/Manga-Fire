import re

filepath = 'app/src/main/java/com/fire/mangareader/util/MangaDownloader.java'
with open(filepath, 'r') as f:
    content = f.read()

# Add isCancelled flag
content = content.replace('public class MangaDownloader {', 'public class MangaDownloader {\n    public static volatile boolean isCancelled = false;\n')

# Add check inside the download loop
loop_target = """for (int i = 0; i < imageUrls.size(); i++) { final int index = i; futures.add(executor.submit(() -> {"""
loop_replace = """for (int i = 0; i < imageUrls.size(); i++) { final int index = i; futures.add(executor.submit(() -> {
                                        if (isCancelled) return;
"""
content = content.replace(loop_target, loop_replace)

with open(filepath, 'w') as f:
    f.write(content)
print("Added isCancelled to MangaDownloader")
