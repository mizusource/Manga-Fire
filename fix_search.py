with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchScreen.kt", "r") as f:
    content = f.read()

# Find the end of the FilterSheetContent function properly
marker = 'Spacer(modifier = Modifier.height(32.dp))\n    }\n}'
first_occurrence = content.find(marker)

if first_occurrence != -1:
    clean_content = content[:first_occurrence + len(marker)]
    with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchScreen.kt", "w") as f:
        f.write(clean_content)

