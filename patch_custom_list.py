import re

# Fix CustomListManagerActivity
with open("app/src/main/java/com/fire/mangareader/presentation/activity/CustomListManagerActivity.java", "r") as f:
    content = f.read()

content = content.replace("list.getListName()", "list.getName()")
content = content.replace("CustomListEntity(id, listName, System.currentTimeMillis())", "CustomListEntity(id, listName, \"\", \"\")")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CustomListManagerActivity.java", "w") as f:
    f.write(content)

# Fix CustomListAdapter
with open("app/src/main/java/com/fire/mangareader/presentation/adapter/CustomListAdapter.java", "r") as f:
    content = f.read()

content = content.replace("item.getListName()", "item.getName()")
content = content.replace("item.getCreatedAt()", "System.currentTimeMillis()")

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/CustomListAdapter.java", "w") as f:
    f.write(content)

