with open('app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('viewModel.saveToHistory(chapter)', 'viewModel.saveToHistory(mangaId, chapter)')

with open('app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt', 'w') as f:
    f.write(content)
