import os

import requests

from dotenv import load_dotenv


load_dotenv()


CSTUDIOS_UPI_URL = (
    "https://api.cstudio.sbs/v1/upi/verify"
)


class UPIProviderError(Exception):
    pass


def get_api_keys() -> list[str]:

    raw_keys = os.getenv(
        "UPI_CSTUDIOS_API_KEYS",
        ""
    )

    return [
        key.strip()
        for key in raw_keys.split(",")
        if key.strip()
    ]


def verify_vpa(vpa: str) -> dict:

    api_keys = get_api_keys()

    if not api_keys:

        raise UPIProviderError(
            "No cStudios UPI API keys configured."
        )

    last_error = None

    for api_key in api_keys:

        try:

            response = requests.get(
                CSTUDIOS_UPI_URL,
                params={
                    "upi_id": vpa
                },
                headers={
                    "X-API-Key": api_key,
                    "Content-Type": "application/json"
                },
                timeout=5
            )

            if response.status_code in {
                401,
                403
            }:

                last_error = (
                    f"Authentication failed "
                    f"for configured API key "
                    f"(HTTP {response.status_code})."
                )

                continue

            if response.status_code == 429:

                last_error = (
                    "UPI provider rate limit reached."
                )

                continue

            if response.status_code >= 500:

                last_error = (
                    f"UPI provider server error "
                    f"(HTTP {response.status_code})."
                )

                continue

            response.raise_for_status()

            data = response.json()

            if data.get("status") != "success":

                raise UPIProviderError(
                    "UPI provider returned "
                    "an unsuccessful response."
                )

            provider_data = data.get(
                "data",
                {}
            )

            return {
                "provider": "cStudios",
                "status": "verified",
                "is_valid": provider_data.get(
                    "is_valid"
                ),
                "account_holder_name": (
                    provider_data.get(
                        "account_holder_name"
                    )
                ),
                "bank": provider_data.get(
                    "bank"
                ),
                "ifsc": provider_data.get(
                    "ifsc"
                ),
                "vpa_status": provider_data.get(
                    "vpa_status"
                )
            }

        except requests.RequestException as error:

            last_error = str(error)

            continue

    raise UPIProviderError(
        last_error
        or "UPI verification failed."
    )