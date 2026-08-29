with open('app/src/main/java/com/fire/mangareader/activity/StorageManagerActivity.java', 'r') as f:
    content = f.read()

content = content.replace('Cache Cleared', 'تم مسح الذاكرة المؤقتة')
content = content.replace('All Downloads Cleared', 'تم مسح جميع التنزيلات')
content = content.replace('Deleted " + finalCount + " read chapters', 'تم حذف " + finalCount + " فصول مقروءة')
content = content.replace('Cache: " + cacheMb + " MB', 'المؤقتة: " + cacheMb + " م.ب')
content = content.replace('Downloads: " + downMb + " MB', 'التنزيلات: " + downMb + " م.ب')

with open('app/src/main/java/com/fire/mangareader/activity/StorageManagerActivity.java', 'w') as f:
    f.write(content)
