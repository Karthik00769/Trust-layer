package com.buddy.trustlayer.data.remote

data class AssessmentRequestDto(
    val content: String,
    val source: String? = null,
    val evidenceType: String = "TEXT",
    val deviceContext: Map<String, String>? = null
)

data class AssessmentResponseDto(
    val id: String? = null,
    val riskScore: Float? = 0f,
    val riskLevel: String? = "LOW",
    val threatDetected: Boolean? = false,
    val summary: String? = "",
    val indicators: List<String>? = emptyList(),
    val recommendation: String? = "",
    val confidence: Float? = null,
    val timestamp: Long? = null
)
