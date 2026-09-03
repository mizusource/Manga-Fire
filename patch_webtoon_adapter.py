import re

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonAdapter.java", "r") as f:
    content = f.read()

content = content.replace("import java.util.List;", "import java.util.List;\nimport com.fire.mangareader.domain.model.reader.Page;")
content = content.replace("private List<String> pages = new ArrayList<>();", "private List<Page> pages = new ArrayList<>();")
content = content.replace("public void setPages(List<String> pages, String cookies, String refererUrl)", "public void setPages(List<Page> pages, String cookies, String refererUrl)")
content = content.replace("holder.bind(pages.get(position), cookies, refererUrl);", "holder.bind(pages.get(position), cookies, refererUrl);")

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonAdapter.java", "w") as f:
    f.write(content)
