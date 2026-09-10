from app.security.url_analyzer import analyze_url
from app.security.threat_intel import check_url


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

    if context["financial_brands"]:
        signals.append("financial_brand_mentioned")

    if context["financial_actions"]:
        signals.append("financial_action")

    if context["account_threats"]:
        threats.append("Account threat or pressure tactic")
        signals.append("account_threat")

    url_results = []
    threat_intelligence_results = []

    for url in context["urls"]:

        # Existing deterministic URL analysis
        url_result = analyze_url(
            url,
            context["financial_brands"]
        )

        url_results.append(url_result)

        signals.extend(url_result["signals"])
        threats.extend(url_result["threats"])

        # External threat intelligence
        threat_result = check_url(url)

        threat_intelligence_results.append(threat_result)

        if threat_result["threat_detected"]:

            signals.append("known_threat_detected")

            for source in threat_result["threat_sources"]:
                signals.append(
                    f"known_threat_{source.lower()}"
                )

            threats.append(
                "URL found in threat intelligence databases"
            )

    signals = list(dict.fromkeys(signals))
    threats = list(dict.fromkeys(threats))

    # Structured security evidence
    evidence = {
        "social_engineering": [],
        "financial_targeting": [],
        "credential_risk": [],
        "url_risk": [],
        "threat_intelligence": []
    }

    # Social engineering evidence
    if "urgency" in signals:
        evidence["social_engineering"].append(
            "Urgency or time-pressure detected"
        )

    if "account_threat" in signals:
        evidence["social_engineering"].append(
            "Account threat or pressure tactic detected"
        )

    # Financial targeting evidence
    if "financial_context" in signals:
        evidence["financial_targeting"].append(
            "Financial context detected"
        )

    if "financial_brand_mentioned" in signals:
        evidence["financial_targeting"].append(
            "Financial brand mentioned"
        )

    if "financial_action" in signals:
        evidence["financial_targeting"].append(
            "Financial action requested"
        )

    # Credential risk evidence
    if "credential_request" in signals:
        evidence["credential_risk"].append(
            "Credential or sensitive-information request detected"
        )

    # URL risk evidence
    url_risk_signals = {
        "suspicious_url_keywords":
            "Suspicious keywords detected in URL",

        "suspicious_tld":
            "Suspicious domain extension detected",

        "ip_address_url":
            "IP address used instead of a domain name",

        "many_subdomains":
            "Unusually complex domain structure detected",

        "http":
            "Unencrypted HTTP link detected",

        "possible_brand_impersonation":
            "Possible financial brand impersonation detected"
    }

    for signal, description in url_risk_signals.items():

        if signal in signals:
            evidence["url_risk"].append(description)

    # Threat intelligence evidence
    if "known_threat_detected" in signals:

        evidence["threat_intelligence"].append(
            "URL matched a known threat intelligence source"
        )

        for source in threat_intelligence_results:

            for threat_source in source["threat_sources"]:

                evidence["threat_intelligence"].append(
                    f"Confirmed by {threat_source}"
                )

    return {
        "threats": threats,
        "signals": signals,
        "evidence": evidence,
        "url_results": url_results,
        "threat_intelligence": threat_intelligence_results
    }