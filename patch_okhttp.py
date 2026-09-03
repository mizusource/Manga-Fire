import re

with open("app/src/main/java/com/fire/mangareader/util/MangaOkHttp.java", "r") as f:
    content = f.read()

import_stmt = "import com.fire.mangareader.data.network.interceptor.DirectIpInterceptor;\n"
if "DirectIpInterceptor" not in content:
    content = content.replace("import com.fire.mangareader.data.network.interceptor.RetryInterceptor;", 
                              "import com.fire.mangareader.data.network.interceptor.RetryInterceptor;\n" + import_stmt)
                              
    interceptor_field = "    public static DirectIpInterceptor directIpInterceptor = new DirectIpInterceptor(null);\n"
    content = content.replace("private static OkHttpClient client;", interceptor_field + "    private static OkHttpClient client;")
    
    add_interceptor = "                    .addInterceptor(new RetryInterceptor())\n                    .addInterceptor(directIpInterceptor);"
    content = content.replace(".addInterceptor(new RetryInterceptor());", add_interceptor)
    
    with open("app/src/main/java/com/fire/mangareader/util/MangaOkHttp.java", "w") as f:
        f.write(content)
    print("Patched MangaOkHttp.java")
