package com.buddy.trustlayer.data.repository

import com.buddy.trustlayer.domain.model.RiskLevel
import com.buddy.trustlayer.domain.model.TrustAssessment
import com.buddy.trustlayer.domain.model.TrustContext
import com.buddy.trustlayer.domain.repository.TrustEngineRepository
import kotlinx.coroutines.delay

class MockTrustEngineRepository : TrustEngineRepository {
    override suspend fun assess(context: TrustContext): TrustAssessment {
        // Simulate processing delay
        delay(2500)
        
        val contentLower = context.content.lowercase() + context.url.lowercase()
        
        // High Risk Scenario
        val isHighRisk = contentLower.contains("urgent") || 
                         contentLower.contains("password") || 
                         contentLower.contains("bank") ||
                         contentLower.contains("suspend") ||
                         contentLower.contains("verify")
                         
        // Medium Risk Scenario
        val isMediumRisk = !isHighRisk && (
                         contentLower.contains("delivery") ||
                         contentLower.contains("confirm") ||
                         contentLower.contains("address") ||
                         contentLower.contains("account")
        )

        return when {
            isHighRisk -> {
                TrustAssessment(
                    contextId = context.id,
                    riskScore = 88f,
                    riskLevel = RiskLevel.HIGH,
                    summary = "High risk indicators found in message. Request exhibits urgency and requests sensitive action.",
                    signals = listOf("Urgency detected", "Financial/Credential keyword", "Suspicious link structure"),
                    recommendation = "Do not click any links or provide personal information. Contact the institution directly through an official channel.",
                    isThreatDetected = true,
                    title = "Suspicious Financial Request",
                    confidence = 0.92f
                )
            }
            isMediumRisk -> {
                TrustAssessment(
                    contextId = context.id,
                    riskScore = 55f,
                    riskLevel = RiskLevel.MEDIUM,
                    summary = "Moderate risk indicators found. Message resembles common logistics or account phishing attempts.",
                    signals = listOf("Unexpected request", "Request for confirmation"),
                    recommendation = "Verify the source before proceeding. If expecting a delivery, check the tracking number on the official carrier website.",
                    isThreatDetected = true,
                    title = "Potentially Unsafe Request",
                    confidence = 0.78f
                )
            }
            else -> {
                TrustAssessment(
                    contextId = context.id,
                    riskScore = 5f,
                    riskLevel = RiskLevel.LOW,
                    summary = "No immediate threats detected in the provided context.",
                    signals = listOf("Standard vocabulary", "No urgent requests"),
                    recommendation = "Message appears safe, but always remain vigilant.",
                    isThreatDetected = false,
                    title = "Standard Communication",
                    confidence = 0.98f
                )
            }
        }
    }
}
