import os
import re

files_to_patch = [
    'app/src/main/java/com/fire/mangareader/utils/AppAdminSettings.java',
    'app/src/main/java/com/fire/mangareader/utils/GlobalMangaStatsManager.java',
    'app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java',
    'app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java',
    'app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java',
    'app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java',
    'app/src/main/java/com/fire/mangareader/network/CommentRepository.java'
]

for file_path in files_to_patch:
    if not os.path.exists(file_path): continue
    with open(file_path, 'r') as f:
        content = f.read()

    # Remove all firebase imports
    content = re.sub(r'import com\.google\.firebase\..*?;\n?', '', content)

    with open(file_path, 'w') as f:
        f.write(content)
