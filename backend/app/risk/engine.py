def calculate_risk(security_result: dict) -> dict:

    score = 0

    signals = security_result["signals"]

    if "possible_brand_impersonation" in signals:
        score += 20

    if "credential_request" in signals:
        score += 20

    if "account_threat" in signals:
        score += 15

    if "suspicious_url_keywords" in signals:
        score += 10

    if "suspicious_tld" in signals:
        score += 10

    if "ip_address_url" in signals:
        score += 15

    if "many_subdomains" in signals:
        score += 10

    if "http" in signals:
        score += 5

    if "urgency" in signals:
        score += 10

    if "financial_action" in signals:
        score += 5

    if "financial_context" in signals:
        score += 5

    if "financial_brand_mentioned" in signals:
        score += 5

    # UPI security signals
    if "upi_invalid_vpa" in signals:
        score += 15

    threat_intelligence = security_result.get(
        "threat_intelligence",
        []
    )

    for threat_result in threat_intelligence:

        if threat_result.get("threat_detected"):

            score += 40

            if len(threat_result.get("threat_sources", [])) >= 2:
                score += 10

    score = min(score, 100)

    if score < 15:
        classification = "SAFE"

    elif score <= 60:
        classification = "SUSPICIOUS"

    else:
        classification = "HIGH_RISK"

    return {
        "risk_score": score,
        "classification": classification
    }