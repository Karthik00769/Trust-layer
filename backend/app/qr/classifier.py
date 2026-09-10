from urllib.parse import urlparse


def classify_payload(payload: str) -> str:

    payload = payload.strip()

    if not payload:
        return "UNKNOWN"

    parsed = urlparse(payload)

    if parsed.scheme.lower() in {
        "http",
        "https"
    }:
        return "URL"

    if parsed.scheme.lower() == "upi":
        return "UPI"

    return "TEXT"