with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    lines = f.readlines()

new_imports = [
    "import kotlinx.coroutines.launch\n",
    "import android.widget.Toast\n",
    "import androidx.compose.material.icons.filled.Refresh\n"
]

for imp in new_imports:
    if imp not in lines:
        lines.insert(2, imp)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.writelines(lines)
