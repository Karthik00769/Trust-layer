from urllib.parse import parse_qs, urlparse


def parse_upi_payload(payload: str) -> dict:
    """
    Parse a UPI payment URI into structured fields.

    This function only extracts information.
    It does not determine whether the payment is safe.
    """

    if not payload:
        return {
            "valid": False,
            "errors": ["UPI payload is empty"]
        }

    value = payload.strip()

    parsed = urlparse(value)

    if parsed.scheme.lower() != "upi":
        return {
            "valid": False,
            "errors": ["Payload is not a UPI URI"]
        }

    if parsed.netloc.lower() != "pay":
        return {
            "valid": False,
            "errors": ["Unsupported UPI URI action"]
        }

    query = parse_qs(
        parsed.query,
        keep_blank_values=True
    )

    vpa = query.get("pa", [None])[0]
    payee_name = query.get("pn", [None])[0]
    amount = query.get("am", [None])[0]
    currency = query.get("cu", [None])[0]

    errors = []

    if not vpa:
        errors.append(
            "UPI payment address (pa) is missing"
        )

    return {
        "valid": len(errors) == 0,
        "scheme": parsed.scheme.lower(),
        "action": parsed.netloc.lower(),
        "vpa": vpa,
        "payee_name": payee_name,
        "amount": amount,
        "currency": currency,
        "errors": errors
    }