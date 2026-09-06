import re


def extract_context(text: str) -> dict:
    urls = re.findall(r"https?://[^\s]+", text)

    urgency_keywords = [
        "immediately",
        "urgent",
        "urgently",
        "now",
        "today",
        "within 24 hours",
        "action required",
        "act now",
        "account will be blocked",
        "account will be suspended"
    ]

    financial_keywords = [
        "bank",
        "account",
        "upi",
        "payment",
        "transaction",
        "kyc",
        "credit card",
        "debit card",
        "loan",
        "refund"
    ]

    credential_keywords = [
        "otp",
        "password",
        "pin",
        "cvv",
        "verification code",
        "login"
    ]

    lower_text = text.lower()

    urgency_signals = [
        keyword
        for keyword in urgency_keywords
        if keyword in lower_text
    ]

    financial_signals = [
        keyword
        for keyword in financial_keywords
        if keyword in lower_text
    ]

    credential_signals = [
        keyword
        for keyword in credential_keywords
        if keyword in lower_text
    ]

    return {
        "urls": urls,
        "urgency_signals": urgency_signals,
        "financial_signals": financial_signals,
        "credential_signals": credential_signals
    }