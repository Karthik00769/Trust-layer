import cv2


def decode_qr(image_path: str) -> str:

    image = cv2.imread(image_path)

    if image is None:
        raise ValueError(
            "Unable to read QR image."
        )

    detector = cv2.QRCodeDetector()

    decoded_text, points, _ = detector.detectAndDecode(image)

    if not decoded_text:
        raise ValueError(
            "No readable QR code found in the image."
        )

    return decoded_text