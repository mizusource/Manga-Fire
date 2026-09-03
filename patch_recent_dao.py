filepath = 'app/src/main/java/com/fire/mangareader/data/local/dao/RecentDao.kt'
with open(filepath, 'r') as f:
    content = f.read()

if 'deleteRecent' not in content:
    content = content.replace('suspend fun clearHistory()', 'suspend fun clearHistory()\n\n    @Query("DELETE FROM recent_history WHERE id = :id")\n    suspend fun deleteRecent(id: String)')

with open(filepath, 'w') as f:
    f.write(content)
