def calculate_risk(security_result: dict) -> dict:
    score = 0

    signals = security_result["signals"]

    if "urgency" in signals:
        score += 25

    if "financial_context" in signals:
        score += 10

    if "credential_request" in signals:
        score += 30

    if "external_link" in signals:
        score += 20

    score = min(score, 100)

    if score <= 30:
        classification = "SAFE"
    elif score <= 60:
        classification = "SUSPICIOUS"
    else:
        classification = "HIGH_RISK"

    return {
        "risk_score": score,
        "classification": classification
    }