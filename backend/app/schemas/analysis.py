from pydantic import BaseModel


class TextAnalysisRequest(BaseModel):
    text: str


class BuddyResponse(BaseModel):
    risk_score: int
    classification: str
    threats: list[str]
    explanation: str
    recommended_action: str
    rag_knowledge: list[dict]