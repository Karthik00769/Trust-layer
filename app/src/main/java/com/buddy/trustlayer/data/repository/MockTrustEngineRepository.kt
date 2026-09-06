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
        val isHighRisk = contentLower.contains("urgent") || 
                         contentLower.contains("password") || 
                         contentLower.contains("bank") ||
                         contentLower.contains("verify")

        return if (isHighRisk) {
            TrustAssessment(
                contextId = context.id,
                riskScore = 85f,
                riskLevel = RiskLevel.HIGH,
                summary = "High risk indicators found in message. Request exhibits urgency and financial keywords.",
                signals = listOf("Urgency detected", "Financial request", "Suspicious link structure"),
                recommendation = "Do not click any links or provide personal information. Contact the institution directly.",
                isThreatDetected = true,
                title = "Suspicious Financial Request"
            )
        } else {
            TrustAssessment(
                contextId = context.id,
                riskScore = 15f,
                riskLevel = RiskLevel.LOW,
                summary = "No immediate threats detected in the provided context.",
                signals = listOf("Standard vocabulary", "No urgent requests", "No suspicious links"),
                recommendation = "Message appears safe, but always remain vigilant.",
                isThreatDetected = false,
                title = "Standard Communication"
            )
        }
    }
}
