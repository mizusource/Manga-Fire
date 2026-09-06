import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    lines = f.readlines()

for i in range(len(lines)):
    if 'Pattern.compile(' in lines[i - 1] if i > 0 else False:
        if '<img' in lines[i]:
            lines[i] = '                        "<img[^>]+(?:data-src|data-lazy-src|src)=[\\\"\\\'](https?://[^\\\"\\\']+\\\\.(?:jpg|jpeg|png|webp|gif|avif)[^\\\"\\\']*)[\\\"\\\'][^>]*>",\n'
        elif '\\"(https?:' in lines[i]:
            lines[i] = '                            "\\\\\\"(https?://[^\\\\\\"]+\\\\.(?:jpg|jpeg|png|webp|gif|avif)[^\\\\\\"]*)\\\\\\"",\n'

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.writelines(lines)

