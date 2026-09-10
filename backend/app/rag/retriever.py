import json
from pathlib import Path


KNOWLEDGE_BASE_PATH = (
    Path(__file__).parent / "knowledge_base.json"
)


def load_knowledge_base() -> list[dict]:

    with open(
        KNOWLEDGE_BASE_PATH,
        "r",
        encoding="utf-8"
    ) as file:

        return json.load(file)


def retrieve_knowledge(
    security_result: dict,
    context: dict | None = None,
    max_results: int = 3
) -> list[dict]:

    knowledge_base = load_knowledge_base()

    signals = security_result.get("signals", [])
    threats = security_result.get("threats", [])

    context = context or {}

    search_terms = []

    # Structured security signals
    search_terms.extend(signals)

    # Human-readable security threats
    search_terms.extend(threats)

    # Raw extracted context
    for values in context.values():

        if isinstance(values, list):
            search_terms.extend(values)

    search_text = " ".join(
        str(term).lower()
        for term in search_terms
    )

    signal_set = {
        signal.lower()
        for signal in signals
    }

    matches = []

    for entry in knowledge_base:

        signal_matches = 0
        keyword_matches = 0

        # Match structured security signals
        for signal in entry.get("signals", []):

            if signal.lower() in signal_set:
                signal_matches += 1

        # Match natural-language keywords
        for keyword in entry.get("keywords", []):

            if keyword.lower() in search_text:
                keyword_matches += 1

        total_score = (
            signal_matches * 3
            + keyword_matches
        )

        if total_score > 0:

            matches.append(
                {
                    "entry": entry,
                    "score": total_score,
                    "signal_matches": signal_matches,
                    "keyword_matches": keyword_matches
                }
            )

    matches.sort(
        key=lambda item: (
            item["score"],
            item["signal_matches"],
            item["keyword_matches"]
        ),
        reverse=True
    )

    return [
        item["entry"]
        for item in matches[:max_results]
    ]