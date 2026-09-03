import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/home/HomeScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import androidx.compose.foundation.pager.HorizontalPager',
                          'import androidx.compose.foundation.pager.HorizontalPager\nimport androidx.compose.foundation.ExperimentalFoundationApi')

content = content.replace('@Composable\nfun HeroBanner',
                          '@OptIn(ExperimentalFoundationApi::class)\n@Composable\nfun HeroBanner')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched HomeScreen.kt")
