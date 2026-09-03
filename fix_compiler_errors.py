import re

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonPageHolder.java", "r") as f:
    holder_content = f.read()

# Fix the page variable issue. The bind method parameter is:
# public void bind(com.fire.mangareader.domain.model.reader.Page page, String cookies, String refererUrl)
# We need to make sure we make it final to use in the Thread lambda, or use it directly.
holder_content = holder_content.replace(
    "public void bind(com.fire.mangareader.domain.model.reader.Page page, String cookies, String refererUrl) {",
    "public void bind(final com.fire.mangareader.domain.model.reader.Page page, String cookies, String refererUrl) {"
)

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonPageHolder.java", "w") as f:
    f.write(holder_content)

# Fix ChapterReaderActivity
with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    activity_content = f.read()

# 380: adapter.setPages(localPages, null, null);
# where localPages is List<String>
local_pages_replacement = """
                            List<com.fire.mangareader.domain.model.reader.Page> pageList = new java.util.ArrayList<>();
                            for (int i = 0; i < localPages.size(); i++) {
                                pageList.add(new com.fire.mangareader.domain.model.reader.Page(i, localPages.get(i), localPages.get(i), null));
                            }
                            adapter.setPages(pageList, null, null);
"""
activity_content = re.sub(r'adapter\.setPages\(localPages,\s*null,\s*null\);', local_pages_replacement.strip(), activity_content)

# 542: adapter.setPages(pages, cookies, refererUrl);
# Check where `pages` is defined and what it is.
pages_replacement = """
                        List<com.fire.mangareader.domain.model.reader.Page> pageList = new java.util.ArrayList<>();
                        for (int i = 0; i < pages.size(); i++) {
                            pageList.add(new com.fire.mangareader.domain.model.reader.Page(i, pages.get(i), pages.get(i), null));
                        }
                        adapter.setPages(pageList, cookies, refererUrl);
"""
# careful with replace, let's see where this is. It's likely in a method where `pages` is List<String>.
# Let's just do a string replace for this specific line.
activity_content = activity_content.replace("adapter.setPages(pages, cookies, refererUrl);", pages_replacement.strip())

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(activity_content)

