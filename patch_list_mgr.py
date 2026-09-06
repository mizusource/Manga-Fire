import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CustomListManagerActivity.java", "r") as f:
    content = f.read()

nav_intent = """
                android.content.Intent intent = new android.content.Intent(this, CustomListDetailActivity.class);
                intent.putExtra("listId", list.getListId());
                intent.putExtra("listName", list.getName());
                startActivity(intent);
"""

content = content.replace('Toast.makeText(this, "Opening list: " + list.getName(), Toast.LENGTH_SHORT).show();', nav_intent.strip())

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CustomListManagerActivity.java", "w") as f:
    f.write(content)

