with open('app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt', 'r') as f:
    content = f.read()

# Let's fix the brackets. BottomNavigationBar ends with `        }\n    }\n}\n\n@Composable`
content = content.replace('        }\n    }\n}\n}\n@Composable', '        }\n    }\n}\n\n@Composable')
content = content.replace('        }\n    }\n}\n}\n\n@Composable', '        }\n    }\n}\n\n@Composable')

with open('app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt', 'w') as f:
    f.write(content)
