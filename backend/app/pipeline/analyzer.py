from app.schemas.analysis import BuddyResponse
from app.extraction.context import extract_context
from app.security.engine import analyze_security_signals
from app.risk.engine import calculate_risk



def analyze_text(text: str) -> BuddyResponse:

    context = extract_context(text)

    security_result = analyze_security_signals(context)

    risk_result = calculate_risk(security_result)

    if risk_result["classification"] == "HIGH_RISK":
        explanation = (
            "The message contains multiple indicators commonly associated "
            "with financial scams or phishing attempts."
        )
        recommended_action = (
            "Do not click links, provide credentials, share OTPs, "
            "or make payments."
        )

    elif risk_result["classification"] == "SUSPICIOUS":
        explanation = (
            "The message contains some suspicious characteristics "
            "that require caution."
        )
        recommended_action = (
            "Verify the message through an official source before taking action."
        )

    else:
        explanation = (
            "No major suspicious indicators were detected by the current "
            "security rules."
        )
        recommended_action = (
            "No immediate action is required, but remain cautious."
        )


    return BuddyResponse(
        risk_score=risk_result["risk_score"],
        classification=risk_result["classification"],
        threats=security_result["threats"],
        explanation=explanation,
        recommended_action=recommended_action
    )