from fastapi import APIRouter

from app.schemas.analysis import (
    TextAnalysisRequest,
    URLAnalysisRequest,
    BuddyResponse
)

from app.pipeline.analyzer import (
    analyze_text,
    analyze_input
)


router = APIRouter()


@router.post(
    "/analyze/text",
    response_model=BuddyResponse
)
def analyze_text_endpoint(
    request: TextAnalysisRequest
):

    return analyze_text(
        request.text
    )


@router.post(
    "/analyze/url",
    response_model=BuddyResponse
)
def analyze_url_endpoint(
    request: URLAnalysisRequest
):

    return analyze_input(
        request.url
    )