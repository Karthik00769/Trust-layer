import os
import requests


from dotenv import load_dotenv

from app.security.threat_cache import get_cached_result, cache_result
from app.security.url_normalizer import normalize_url


load_dotenv()


URLHAUS_API_URL = "https://urlhaus-api.abuse.ch/v1/url/"
PHISHSTATS_API_URL = "https://api.phishstats.info/api/phishing"


def check_urlhaus(url: str) -> dict:
    auth_key = os.getenv("URLHAUS_AUTH_KEY")

    if not auth_key:
        return {
            "status": "not_configured",
            "threat_detected": False,
            "message": "URLhaus API key is not configured"
        }

    try:
        response = requests.post(
            URLHAUS_API_URL,
            headers={
                "Auth-Key": auth_key
            },
            data={
                "url": url
            },
            timeout=5
        )

        response.raise_for_status()

        data = response.json()

        if data.get("query_status") == "ok":
            return {
                "status": "found",
                "threat_detected": True,
                "url_status": data.get("url_status"),
                "threat": data.get("threat"),
                "tags": data.get("tags", [])
            }

        return {
            "status": "not_found",
            "threat_detected": False
        }

    except requests.RequestException as error:
        return {
            "status": "error",
            "threat_detected": False,
            "message": str(error)
        }


def check_phishstats(url: str) -> dict:
    try:
        response = requests.get(
            PHISHSTATS_API_URL,
            params={
                "_where": f"(url,eq,{url})"
            },
            timeout=5
        )

        response.raise_for_status()

        data = response.json()

        if isinstance(data, list) and len(data) > 0:
            return {
                "status": "found",
                "threat_detected": True,
                "matches": len(data)
            }

        return {
            "status": "not_found",
            "threat_detected": False
        }

    except requests.RequestException as error:
        return {
            "status": "error",
            "threat_detected": False,
            "message": str(error)
        }


def check_url(url: str) -> dict:

    # Normalize URL before cache lookup and API calls
    url = normalize_url(url)

    # Check cache first
    cached_result = get_cached_result(url)

    if cached_result is not None:
        return cached_result

    # Cache miss → call threat intelligence APIs
    urlhaus_result = check_urlhaus(url)
    phishstats_result = check_phishstats(url)

    urlhaus_threat = urlhaus_result["threat_detected"]
    phishstats_threat = phishstats_result["threat_detected"]

    threat_sources = []

    if urlhaus_threat:
        threat_sources.append("URLhaus")

    if phishstats_threat:
        threat_sources.append("PhishStats")

    threat_detected = len(threat_sources) > 0

    result = {
        "url": url,
        "threat_detected": threat_detected,
        "threat_sources": threat_sources,
        "urlhaus": urlhaus_result,
        "phishstats": phishstats_result
    }

    # Do not cache temporary API errors
    if (
        urlhaus_result.get("status") != "error"
        or phishstats_result.get("status") != "error"
    ):
        cache_result(url, result)

    return result