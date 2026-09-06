package com.buddy.trustlayer.domain.model

import java.util.UUID

/**
 * Represents the result returned by the Trust Engine.
 */
data class TrustAssessment(
    val id: String = UUID.randomUUID().toString(),
    val contextId: String = "",
    val riskScore: Float,
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val summary: String,
    val signals: List<String> = emptyList(),
    val recommendation: String = "",
    val isThreatDetected: Boolean,
    val title: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
