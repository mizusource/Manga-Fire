import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaGlideModule.java", "r") as f:
    text = f.read()

pattern = r'registry\.replace\(GlideUrl\.class, InputStream\.class, new OkHttpUrlLoader\.Factory\(com\.fire\.mangareader\.util\.MangaOkHttp\.getClient\(\)\)\);'

replacement = r'''
        // Default client without heavy interceptors
        OkHttpClient defaultClient = client;
        
        // Special client with CookieJar and Interceptors for specific domains
        OkHttpClient specialClient = com.fire.mangareader.util.MangaOkHttp.getClient();

        registry.replace(
                GlideUrl.class, 
                InputStream.class, 
                new SmartOkHttpUrlLoader.Factory(specialClient, defaultClient)
        );
'''

text = re.sub(pattern, replacement, text)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaGlideModule.java", "w") as f:
    f.write(text)
