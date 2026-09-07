from app.extraction.context import extract_context
from app.security.engine import analyze_security_signals
from app.rag.retriever import retrieve_knowledge
from app.risk.engine import calculate_risk


def analyze_text(text: str) -> dict:

    # Step 1: Extract structured context
    context = extract_context(text)

    # Step 2: Analyze deterministic security signals
    security_result = analyze_security_signals(context)

    # Step 3: Calculate deterministic risk score
    risk_result = calculate_risk(security_result)

    # Step 4: Retrieve relevant security knowledge
    rag_knowledge = retrieve_knowledge(
        security_result,
        context=context
    )

    # Step 5: Generate explanation and recommended action
    if risk_result["classification"] == "HIGH_RISK":

        explanation = (
            "The message contains multiple indicators commonly "
            "associated with financial scams or phishing attempts."
        )

        recommended_action = (
            "Do not click links, provide credentials, "
            "share OTPs, or make payments."
        )

    elif risk_result["classification"] == "SUSPICIOUS":

        explanation = (
            "The message contains some indicators that "
            "require additional caution."
        )

        recommended_action = (
            "Verify the request through an official source "
            "before taking any action."
        )

    else:

        explanation = (
            "No major suspicious indicators were detected "
            "by the current security rules."
        )

        recommended_action = (
            "No immediate action is required, "
            "but remain cautious."
        )

    return {
        "risk_score": risk_result["risk_score"],
        "classification": risk_result["classification"],
        "threats": security_result["threats"],
        "explanation": explanation,
        "recommended_action": recommended_action,
        "rag_knowledge": rag_knowledge
    }