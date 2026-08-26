import re

with open('app/src/main/java/com/fire/mangareader/network/CloudflareBypassDialog.java', 'r') as f:
    content = f.read()

content = content.replace('String cookies = CookieManager.getInstance().getCookie(pageUrl);', 'final String cookies = CookieManager.getInstance().getCookie(pageUrl) != null ? CookieManager.getInstance().getCookie(pageUrl) : "";')
content = content.replace('if (cookies == null) cookies = "";', '')

with open('app/src/main/java/com/fire/mangareader/network/CloudflareBypassDialog.java', 'w') as f:
    f.write(content)
