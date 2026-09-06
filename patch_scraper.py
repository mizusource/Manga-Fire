import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

content = content.replace(
    '\"<img[^>]+(?:data-src|data-lazy-src|src)=[\"\'](https?://[^\"\']+\\.(?:jpg|jpeg|png|webp|gif|avif)[^\"\']*)[\"\'][^>]*>\"',
    '\"<img[^>]+(?:data-src|data-lazy-src|src)=[\\\"\\\'](https?://[^\\\"\\\']+\\\\.(?:jpg|jpeg|png|webp|gif|avif)[^\\\"\\\']*)[\\\"\\\'][^>]*>\"'
)

content = content.replace(
    '\"\"(https?://[^\"]+\\.(?:jpg|jpeg|png|webp|gif|avif)[^\"]*)\"\"',
    '\"\\\\\"(https?://[^\\\\\"]+\\\\.(?:jpg|jpeg|png|webp|gif|avif)[^\\\\\"]*)\\\\\"\"'
)

content = content.replace('replace("\\/", "/")', 'replace("\\\\/", "/")')

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)

