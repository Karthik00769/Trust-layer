from fastapi import APIRouter

from app.schemas.analysis import TextAnalysisRequest, BuddyResponse
from app.pipeline.analyzer import analyze_text


router = APIRouter()


@router.post("/analyze/text", response_model=BuddyResponse)
def analyze_text_endpoint(request: TextAnalysisRequest):
    return analyze_text(request.text)