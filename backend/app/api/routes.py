from fastapi import APIRouter

from app.schemas.analysis import (
    TextAnalysisRequest,
    URLAnalysisRequest,
    QRAnalysisRequest,
    BuddyResponse
)

from app.pipeline.analyzer import (
    analyze_text,
    analyze_input,
    analyze_upi_payload
)

from app.qr import process_qr


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


@router.post(
    "/analyze/qr",
    response_model=BuddyResponse
)
def analyze_qr_endpoint(
    request: QRAnalysisRequest
):

    qr_result = process_qr(
        request.image_path
    )

    payload = qr_result["payload"]
    payload_type = qr_result["payload_type"]

    if payload_type == "URL":
        return analyze_input(payload)

    if payload_type == "TEXT":
        return analyze_text(payload)

    if payload_type == "UPI":
        return analyze_upi_payload(payload)

    raise ValueError(
        "Unsupported QR payload."
    )