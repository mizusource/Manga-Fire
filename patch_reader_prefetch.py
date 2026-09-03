import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

old_config_pattern = r'com\.fire\.mangareader\.domain\.model\.parser\.ExtractorConfig config = new com\.fire\.mangareader\.domain\.model\.parser\.ExtractorConfig\([\s\S]*?\);'

new_config = """
                com.fire.mangareader.domain.model.parser.ExtractorConfig config = com.fire.mangareader.data.parser.ParserConfigManager.INSTANCE.getExtractor("PagesPrefetch");
                if (config == null) {
                    java.util.Map<String, com.fire.mangareader.domain.model.parser.SelectorConfig> selectors = new java.util.HashMap<>();
                    selectors.put("url", new com.fire.mangareader.domain.model.parser.SelectorConfig("", "data-src", null));
                    
                    java.util.List<com.fire.mangareader.domain.model.parser.UrlTransformation> transforms = new java.util.ArrayList<>();
                    transforms.add(new com.fire.mangareader.domain.model.parser.UrlTransformation(
                            com.fire.mangareader.domain.model.parser.TransformationType.SUBDOMAIN_REPLACE, 
                            "^//", "https://"));
                            
                    config = new com.fire.mangareader.domain.model.parser.ExtractorConfig(
                            "PagesPrefetch",
                            com.fire.mangareader.domain.model.parser.ExtractorType.PAGES,
                            ".reading-content img, .page-break img, #vungdoc img, .vung-doc img, .chapter-video-frame img",
                            new java.util.HashMap<>(),
                            new java.util.ArrayList<>(),
                            selectors,
                            com.fire.mangareader.domain.model.parser.ResponseFormat.HTML,
                            transforms
                    );
                }
"""

if 'com.fire.mangareader.data.parser.ParserConfigManager.INSTANCE.getExtractor("PagesPrefetch")' not in content:
    content = re.sub(old_config_pattern, new_config.strip(), content, count=1, flags=re.DOTALL)
    with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
        f.write(content)
    print("ChapterReaderActivity updated to use ParserConfigManager")
