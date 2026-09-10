from app.extraction.context import extract_context
from app.security.engine import analyze_security_signals
from app.risk.engine import calculate_risk
from app.rag.retriever import retrieve_knowledge
from app.llm.fallback import FallbackLLMClient


def _generate_buddy_response(
    message: str,
    security_result: dict,
    risk_result: dict,
    rag_knowledge: list[dict]
) -> dict:
    """
    Run the shared RAG + LLM response generation stage.

    The LLM explains the deterministic security result.
    It does not determine the final risk score or classification.
    """

    llm_client = FallbackLLMClient()

    llm_result = llm_client.analyze(
        message=message,
        security_evidence={
            "signals": security_result["signals"],
            "threats": security_result["threats"],
            "evidence": security_result["evidence"],
            "url_results": security_result["url_results"],
            "threat_intelligence": (
                security_result["threat_intelligence"]
            ),
            "risk_result": risk_result
        },
        rag_knowledge=rag_knowledge
    )

    return {
        "risk_score": risk_result["risk_score"],
        "classification": risk_result["classification"],
        "threats": security_result["threats"],
        "explanation": llm_result.explanation,
        "recommended_action": llm_result.recommended_action,
        "rag_knowledge": rag_knowledge
    }


def analyze_input(text: str) -> dict:

    context = extract_context(text)

    security_result = analyze_security_signals(
        context
    )

    risk_result = calculate_risk(
        security_result
    )

    rag_knowledge = retrieve_knowledge(
        security_result,
        context=context
    )

    return _generate_buddy_response(
        message=text,
        security_result=security_result,
        risk_result=risk_result,
        rag_knowledge=rag_knowledge
    )


def analyze_text(text: str) -> dict:

    return analyze_input(text)


def analyze_upi_payload(payload: str) -> dict:
    """
    Analyze a UPI payment payload using Buddy's
    shared Risk -> RAG -> LLM pipeline.

    UPI-specific analysis happens before the shared
    finalization stage.
    """

    from app.upi import analyze_upi

    upi_result = analyze_upi(
        payload
    )

    security_result = {
        "signals": upi_result["signals"],
        "threats": upi_result["threats"],
        "evidence": {
            "upi": [
                upi_result["verification"]
            ]
        },
        "url_results": [],
        "threat_intelligence": []
    }

    risk_result = calculate_risk(
        security_result
    )

    rag_knowledge = retrieve_knowledge(
        security_result,
        context=upi_result["parsed"]
    )

    return _generate_buddy_response(
        message=payload,
        security_result=security_result,
        risk_result=risk_result,
        rag_knowledge=rag_knowledge
    )