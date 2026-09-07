import time


CACHE_TTL = 3600

_cache = {}


def get_cached_result(url: str):
    cached = _cache.get(url)

    if cached is None:
        return None

    result, timestamp = cached

    if time.time() - timestamp > CACHE_TTL:
        del _cache[url]
        return None

    return result


def cache_result(url: str, result: dict):
    _cache[url] = (
        result,
        time.time()
    )