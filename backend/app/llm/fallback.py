from app.llm.client import LLMClient
from app.llm.schemas import LLMAnalysis
from app.llm.groq_client import GroqClient
from app.llm.gemini_client import GeminiClient


class FallbackLLMClient(LLMClient):

    def __init__(
        self,
        primary: LLMClient | None = None,
        fallback: LLMClient | None = None
    ):

        self.primary = primary or GroqClient()
        self.fallback = fallback or GeminiClient()

    def analyze(
        self,
        message: str,
        security_evidence: dict,
        rag_knowledge: list[dict]
    ) -> LLMAnalysis:

        try:

            return self.primary.analyze(
                message,
                security_evidence,
                rag_knowledge
            )

        except Exception as primary_error:

            print(
                f"Primary LLM failed: {primary_error}"
            )

            try:

                return self.fallback.analyze(
                    message,
                    security_evidence,
                    rag_knowledge
                )

            except Exception as fallback_error:

                raise RuntimeError(
                    "Both LLM providers failed. "
                    f"Primary error: {primary_error}. "
                    f"Fallback error: {fallback_error}."
                )