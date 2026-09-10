from abc import ABC, abstractmethod

from app.llm.schemas import LLMAnalysis


class LLMProviderError(Exception):
    """
    Raised when an external LLM provider fails.
    """

    pass


class LLMClient(ABC):

    @abstractmethod
    def analyze(
        self,
        message: str,
        security_evidence: dict,
        rag_knowledge: list[dict]
    ) -> LLMAnalysis:
        pass