from urllib.parse import urlparse, urlunparse


def normalize_url(url: str) -> str:

    parsed = urlparse(url.strip())

    scheme = parsed.scheme.lower()
    hostname = (parsed.hostname or "").lower()

    port = parsed.port

    if port is not None:
        if not (
            (scheme == "https" and port == 443)
            or (scheme == "http" and port == 80)
        ):
            hostname = f"{hostname}:{port}"

    path = parsed.path

    if path == "/":
        path = ""

    normalized = urlunparse(
        (
            scheme,
            hostname,
            path,
            parsed.params,
            parsed.query,
            parsed.fragment
        )
    )

    return normalized
