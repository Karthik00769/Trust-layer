from app.qr.decoder import decode_qr
from app.qr.classifier import classify_payload


def process_qr(image_path: str) -> dict:

    payload = decode_qr(image_path)

    payload_type = classify_payload(payload)

    return {
        "payload": payload,
        "payload_type": payload_type
    }