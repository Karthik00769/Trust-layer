from app.extraction.context import extract_context
from app.security.engine import analyze_security_signals
from app.risk.engine import calculate_risk
from app.rag.retriever import retrieve_knowledge
from app.llm.fallback import FallbackLLMClient


def analyze_text(text: str) -> dict:

    # ---------------------------------------------------------
    # 1. Extract useful context from the message
    # ---------------------------------------------------------

    context = extract_context(text)

    # ---------------------------------------------------------
    # 2. Analyze security signals
    # ---------------------------------------------------------

    security_result = analyze_security_signals(
        context
    )

    # ---------------------------------------------------------
    # 3. Calculate deterministic risk
    # ---------------------------------------------------------

    risk_result = calculate_risk(
        security_result
    )

    # ---------------------------------------------------------
    # 4. Retrieve relevant security knowledge
    # ---------------------------------------------------------

    rag_knowledge = retrieve_knowledge(
        security_result,
        context=context
    )

    # ---------------------------------------------------------
    # 5. Ask the LLM to explain the findings
    #
    # The LLM does NOT determine risk_score or classification.
    # Those values come exclusively from the Risk Engine.
    # ---------------------------------------------------------

    llm_client = FallbackLLMClient()

    llm_result = llm_client.analyze(

        message=text,

        security_evidence={
            "signals": security_result["signals"],
            "threats": security_result["threats"],
            "evidence": security_result["evidence"],
            "url_results": security_result["url_results"],
            "threat_intelligence": (
                security_result["threat_intelligence"]
            )
        },

        rag_knowledge=rag_knowledge
    )

    # ---------------------------------------------------------
    # 6. Build final Buddy response
    # ---------------------------------------------------------

    return {
        "risk_score": risk_result["risk_score"],
        "classification": risk_result["classification"],
        "threats": security_result["threats"],
        "explanation": llm_result.explanation,
        "recommended_action": llm_result.recommended_action,
        "rag_knowledge": rag_knowledge
    }