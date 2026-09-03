import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

# We look for the part where ExtractorConfig is built inside fetchChapterPages
old_config_pattern = r'com\.fire\.mangareader\.domain\.model\.parser\.ExtractorConfig config = new com\.fire\.mangareader\.domain\.model\.parser\.ExtractorConfig\([\s\S]*?\);'

new_config = """
                com.fire.mangareader.domain.model.parser.ExtractorConfig config = com.fire.mangareader.data.parser.ParserConfigManager.INSTANCE.getExtractor("PagesExtractor");
                if (config == null) {
                    java.util.Map<String, com.fire.mangareader.domain.model.parser.SelectorConfig> selectors = new java.util.HashMap<>();
                    selectors.put("url", new com.fire.mangareader.domain.model.parser.SelectorConfig("", "data-src", null));
                    
                    java.util.List<com.fire.mangareader.domain.model.parser.UrlTransformation> transforms = new java.util.ArrayList<>();
                    transforms.add(new com.fire.mangareader.domain.model.parser.UrlTransformation(
                            com.fire.mangareader.domain.model.parser.TransformationType.REGEX_REPLACE, 
                            "\\\\b(175|350|w190|w320)\\\\b", "")); 
                    transforms.add(new com.fire.mangareader.domain.model.parser.UrlTransformation(
                            com.fire.mangareader.domain.model.parser.TransformationType.SUBDOMAIN_REPLACE, 
                            "^//", "https://"));
                            
                    config = new com.fire.mangareader.domain.model.parser.ExtractorConfig(
                            "PagesExtractor",
                            com.fire.mangareader.domain.model.parser.ExtractorType.PAGES,
                            "div.reading-content img, div.page-break img, .wp-manga-chapter-img, div.single-chapter img, .reader-area img, #readerarea img, .read-container img, .chapter-content img, .reading-content-wrap img, div.entry-content img, div.entry-content p img, div.text-center img, div.text-left img, div[id*='chapter'] img, div[class*='chapter'] img, .main-col img, div.post-content img, .chapter-image img",
                            new java.util.HashMap<>(),
                            new java.util.ArrayList<>(),
                            selectors,
                            com.fire.mangareader.domain.model.parser.ResponseFormat.HTML,
                            transforms
                    );
                }
"""

if 'com.fire.mangareader.data.parser.ParserConfigManager.INSTANCE.getExtractor("PagesExtractor")' not in content:
    content = re.sub(old_config_pattern, new_config.strip(), content, count=1, flags=re.DOTALL)
    with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
        f.write(content)
    print("MangaScraper updated to use ParserConfigManager")
