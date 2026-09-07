import re
from urllib.parse import urlparse


def analyze_url(url: str, detected_brands: list[str] | None = None) -> dict:

    detected_brands = detected_brands or []

    parsed = urlparse(url)

    hostname = parsed.hostname or ""
    path = parsed.path.lower()

    signals = []
    threats = []

    # --------------------------------------------------
    # 1. Check URL scheme
    # --------------------------------------------------

    if parsed.scheme == "https":
        signals.append("https")

    elif parsed.scheme == "http":
        signals.append("http")

        threats.append("Unencrypted HTTP link")

    # --------------------------------------------------
    # 2. Check if hostname is an IP address
    # --------------------------------------------------

    ip_pattern = r"^\d{1,3}(\.\d{1,3}){3}$"

    if re.match(ip_pattern, hostname):
        signals.append("ip_address_url")
        threats.append("IP address used instead of a domain name")

    # --------------------------------------------------
    # 3. Suspicious keywords in domain/path
    # --------------------------------------------------

    suspicious_keywords = [
        "verify",
        "verification",
        "login",
        "secure",
        "account",
        "update",
        "kyc",
        "confirm",
        "signin",
        "authenticate"
    ]

    url_text = f"{hostname}{path}"

    suspicious_keywords_found = [
        keyword
        for keyword in suspicious_keywords
        if keyword in url_text
    ]

    if suspicious_keywords_found:
        signals.append("suspicious_url_keywords")

        threats.append(
            "Suspicious URL keywords: "
            + ", ".join(suspicious_keywords_found)
        )

    # --------------------------------------------------
    # 4. Check suspicious TLDs
    # --------------------------------------------------

    suspicious_tlds = [
        ".xyz",
        ".top",
        ".click",
        ".link",
        ".shop",
        ".online",
        ".site"
    ]

    if any(hostname.endswith(tld) for tld in suspicious_tlds):
        signals.append("suspicious_tld")
        threats.append("Suspicious domain extension")

    # --------------------------------------------------
    # 5. Check excessive subdomains
    # --------------------------------------------------

    domain_parts = hostname.split(".")

    if len(domain_parts) >= 4:
        signals.append("many_subdomains")
        threats.append("Unusually complex domain structure")

    # --------------------------------------------------
    # 6. Detect brand impersonation
    # --------------------------------------------------

    brand_mismatch = []

    for brand in detected_brands:

        if brand.lower() in hostname:

            # The brand appearing in the hostname does not
            # automatically mean it is the official domain.

            official_like_domains = {
                "sbi": ["sbi.co.in"],
                "hdfc": ["hdfcbank.com"],
                "icici": ["icicibank.com"],
                "axis": ["axisbank.com"],
                "kotak": ["kotak.com"]
            }

            official_domains = official_like_domains.get(
                brand.lower(),
                []
            )

            if hostname not in official_domains:
                brand_mismatch.append(brand)

    if brand_mismatch:
        signals.append("possible_brand_impersonation")

        threats.append(
            "Possible financial brand impersonation: "
            + ", ".join(brand_mismatch)
        )

    # --------------------------------------------------
    # Return URL security evidence
    # --------------------------------------------------

    return {
        "url": url,
        "hostname": hostname,
        "signals": signals,
        "threats": threats
    }