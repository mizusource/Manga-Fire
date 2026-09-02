import re

filepath = 'app/src/main/java/com/fire/mangareader/data/database/AppDatabase.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('CachedChapter.class}, version = 5', 'CachedChapter.class, ConfigEntity.class}, version = 6')
content = content.replace('public abstract CacheDao cacheDao(); // تم إضافة الـ DAO الخاص بالتخزين الذكي', 'public abstract CacheDao cacheDao(); public abstract ConfigDao configDao();')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched AppDatabase.java")
