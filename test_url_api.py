import urllib.request
import json
req = urllib.request.Request(
    'http://127.0.0.1:8000/analyze/url',
    data=json.dumps({"url": "https://sbi-verify-login.xyz"}).encode(),
    headers={'Content-Type': 'application/json'}
)
print(urllib.request.urlopen(req).read().decode())
