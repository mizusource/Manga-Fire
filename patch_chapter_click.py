import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('modifier = Modifier.fillMaxWidth().clickable { onChapterClick(chapterId) }',
                          'modifier = Modifier.fillMaxWidth().clickable { viewModel.saveToHistory(chapter); onChapterClick(chapterId) }')

with open(filepath, 'w') as f:
    f.write(content)
