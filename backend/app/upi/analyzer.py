from mcp_india_stack.tools.upi import validate_upi_vpa

from app.upi.parser import parse_upi_payload


def analyze_upi(payload: str) -> dict:
    """
    Analyze a UPI payment payload and produce
    security evidence for Buddy's risk pipeline.

    The analyzer does not calculate the final risk score.
    """

    parsed = parse_upi_payload(payload)

    # The UPI URI itself is invalid or unsupported.
    if not parsed["valid"]:

        return {
            "parsed": parsed,
            "signals": [
                "upi_invalid_payload"
            ],
            "threats": [
                "Invalid or unsupported UPI payment payload"
            ],
            "verification": {
                "vpa_valid": False,
                "known_provider": False,
                "provider_name": None,
                "provider_type": None,
                "warnings": [],
                "errors": parsed["errors"]
            }
        }

    # Validate the VPA using MCP-India-Stack.
    validation = validate_upi_vpa(
        parsed["vpa"]
    )

    signals = []
    threats = []

    vpa_valid = validation.get(
        "valid",
        False
    )

    known_provider = validation.get(
        "known_provider",
        False
    )

    if not vpa_valid:

        signals.append(
            "upi_invalid_vpa"
        )

        threats.append(
            "Invalid UPI payment address"
        )

    elif known_provider:

        signals.append(
            "upi_known_provider"
        )

    else:

        # Unknown provider is uncertainty,
        # NOT evidence of fraud.
        signals.append(
            "upi_unknown_provider"
        )

    verification = {
        "vpa_valid": vpa_valid,
        "known_provider": known_provider,
        "provider_name": validation.get(
            "provider_name"
        ),
        "provider_type": validation.get(
            "provider_type"
        ),
        "warnings": validation.get(
            "warnings",
            []
        ),
        "errors": validation.get(
            "errors",
            []
        )
    }

    return {
        "parsed": parsed,
        "signals": signals,
        "threats": threats,
        "verification": verification
    }