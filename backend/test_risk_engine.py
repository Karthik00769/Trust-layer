from app.risk.engine import calculate_risk


test_cases = [
    {
        "name": "No signals",
        "signals": []
    },
    {
        "name": "Urgency only",
        "signals": ["urgency"]
    },
    {
        "name": "Account threat only",
        "signals": ["account_threat"]
    },
    {
        "name": "Financial context only",
        "signals": ["financial_context"]
    },
    {
        "name": "Urgency + account threat",
        "signals": [
            "urgency",
            "account_threat"
        ]
    },
    {
        "name": "Urgency + account threat + financial context",
        "signals": [
            "urgency",
            "account_threat",
            "financial_context"
        ]
    },
    {
        "name": "Credential request",
        "signals": ["credential_request"]
    },
    {
        "name": "Credential + urgency",
        "signals": [
            "credential_request",
            "urgency"
        ]
    },
    {
        "name": "Brand impersonation",
        "signals": [
            "possible_brand_impersonation"
        ]
    },
    {
        "name": "Suspicious URL",
        "signals": [
            "suspicious_url_keywords",
            "suspicious_tld",
            "possible_brand_impersonation"
        ]
    }
]


for test in test_cases:

    security_result = {
        "signals": test["signals"],
        "threats": [],
        "threat_intelligence": []
    }

    result = calculate_risk(
        security_result
    )

    print(
        f"{test['name']:<50}"
        f"Score: {result['risk_score']:<3}"
        f"Classification: {result['classification']}"
    )