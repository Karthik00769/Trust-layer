import time


VALID_TTL = 24 * 60 * 60
INVALID_TTL = 60 * 60

_cache = {}


def get_cached_result(vpa: str):

    cached = _cache.get(vpa)

    if cached is None:
        return None

    result = cached["result"]
    timestamp = cached["timestamp"]
    ttl = cached["ttl"]

    if time.time() - timestamp > ttl:

        del _cache[vpa]

        return None

    return result


def cache_result(
    vpa: str,
    result: dict
):

    is_valid = result.get("is_valid")

    ttl = (
        VALID_TTL
        if is_valid is True
        else INVALID_TTL
    )

    _cache[vpa] = {
        "result": result,
        "timestamp": time.time(),
        "ttl": ttl
    }


def clear_cache():

    _cache.clear()