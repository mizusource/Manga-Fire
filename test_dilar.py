import requests

url = "https://dilar.tube/api/mangas?search=magic"
headers = {"User-Agent": "Mozilla/5.0"}
try:
    res = requests.get(url, headers=headers)
    print(res.status_code)
    print(res.text[:500])
except Exception as e:
    print(e)
