import re

with open("app/src/main/java/com/fire/mangareader/MangaApp.java", "r") as f:
    content = f.read()

import_stmt = "import com.fire.mangareader.data.parser.ParserConfigManager;\n"
if "ParserConfigManager" not in content:
    content = content.replace("import android.app.Application;", 
                              "import android.app.Application;\n" + import_stmt)
                              
    init_stmt = "        com.fire.mangareader.util.MangaOkHttp.init(this);\n        ParserConfigManager.INSTANCE.init(this);\n"
    content = content.replace("MangaOkHttp.init(this);", init_stmt)
    
    with open("app/src/main/java/com/fire/mangareader/MangaApp.java", "w") as f:
        f.write(content)
print("Patched MangaApp.java")
