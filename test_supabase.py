import urllib.request
import json
import os

SUPABASE_URL = "https://kifdmdoyicfuzvteaphm.supabase.co" # Need to get from SupabaseManager
SUPABASE_KEY = "..."

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()
    import re
    url_match = re.search(r'SUPABASE_URL = "(.*?)";', content)
    key_match = re.search(r'SUPABASE_KEY = "(.*?)";', content)
    if url_match and key_match:
        SUPABASE_URL = url_match.group(1)
        SUPABASE_KEY = key_match.group(1)

req = urllib.request.Request(f"{SUPABASE_URL}/rest/v1/manga_comments?limit=1")
req.add_header('apikey', SUPABASE_KEY)
try:
    response = urllib.request.urlopen(req)
    print(response.read().decode('utf-8'))
except Exception as e:
    print(e)
