from pydantic import BaseModel


class LLMAnalysis(BaseModel):
    explanation: str
    key_reasons: list[str]
    recommended_action: str