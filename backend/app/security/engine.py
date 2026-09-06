def analyze_security_signals(context: dict) -> dict:
    threats = []
    signals = []

    if context["urgency_signals"]:
        threats.append("Urgency manipulation")
        signals.append("urgency")

    if context["financial_signals"]:
        signals.append("financial_context")

    if context["credential_signals"]:
        threats.append("Credential or sensitive-information request")
        signals.append("credential_request")

    if context["urls"]:
        threats.append("External link detected")
        signals.append("external_link")

    return {
        "threats": threats,
        "signals": signals
    }