import re


def extract_context(text: str) -> dict:

    urls = re.findall(r"https?://[^\s]+", text)

    urgency_keywords = [
        "immediately",
        "urgent",
        "urgently",
        "within 24 hours",
        "action required",
        "act now"
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

    financial_brands = [
        "sbi",
        "hdfc",
        "icici",
        "axis",
        "kotak",
        "paytm",
        "phonepe",
        "gpay",
        "google pay"
    ]

    financial_actions = [
        "kyc",
        "verify",
        "verification",
        "payment",
        "refund",
        "transfer",
        "activate",
        "deactivate"
    ]

    account_threat_patterns = [
        "account will be blocked",
        "account will be suspended",
        "account has been blocked",
        "account has been suspended",
        "account will be closed",
        "account will be deactivated"
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

    # Remove URLs before checking for credential requests.
    text_without_urls = re.sub(
        r"https?://[^\s]+",
        "",
        lower_text
    )

    credential_signals = [
        keyword
        for keyword in credential_keywords
        if keyword in text_without_urls
    ]

    detected_brands = [
        brand
        for brand in financial_brands
        if brand in lower_text
    ]

    detected_actions = [
        action
        for action in financial_actions
        if action in lower_text
    ]

    account_threats = [
        pattern
        for pattern in account_threat_patterns
        if pattern in lower_text
    ]

    amounts = re.findall(
        r"(?:₹|rs\.?|inr)\s?[\d,]+(?:\.\d+)?",
        lower_text
    )

    return {
        "urls": urls,
        "urgency_signals": urgency_signals,
        "financial_signals": financial_signals,
        "credential_signals": credential_signals,
        "financial_brands": detected_brands,
        "financial_actions": detected_actions,
        "account_threats": account_threats,
        "amounts": amounts
    }