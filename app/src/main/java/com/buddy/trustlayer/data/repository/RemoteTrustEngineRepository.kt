package com.buddy.trustlayer.data.repository

import com.buddy.trustlayer.data.remote.BuddyResponseDto
import com.buddy.trustlayer.data.remote.TextAnalysisRequestDto
import com.buddy.trustlayer.data.remote.TrustEngineApi
import com.buddy.trustlayer.data.remote.UrlAnalysisRequestDto
import com.buddy.trustlayer.domain.model.RiskLevel
import com.buddy.trustlayer.domain.model.TrustAssessment
import com.buddy.trustlayer.domain.model.TrustContext
import com.buddy.trustlayer.domain.repository.TrustEngineRepository
import retrofit2.HttpException
import java.io.IOException
import java.util.UUID

class RemoteTrustEngineRepository(
    private val api: TrustEngineApi
) : TrustEngineRepository {
    override suspend fun assess(context: TrustContext): TrustAssessment {
        val response: BuddyResponseDto = try {
            val isExplicitUrl = context.evidenceType.equals("URL", ignoreCase = true)
            val hasOnlyUrl = context.content.isBlank() && context.url.isNotBlank()
            val contentIsUrl = context.content.trim().let { 
                (it.startsWith("http://", ignoreCase = true) || it.startsWith("https://", ignoreCase = true)) && !it.contains(" ")
            }

            if (isExplicitUrl || hasOnlyUrl || contentIsUrl) {
                val urlToAnalyze = context.url.takeIf { it.isNotBlank() } ?: context.content.trim()
                api.analyzeUrl(UrlAnalysisRequestDto(url = urlToAnalyze))
            } else {
                val fullText = buildString {
                    append(context.content.trim())
                    if (context.url.isNotBlank() && !context.content.contains(context.url)) {
                        if (isNotEmpty()) append(" ")
                        append(context.url.trim())
                    }
                }
                api.analyzeText(TextAnalysisRequestDto(text = fullText))
            }
        } catch (e: IOException) {
            throw Exception("Network connection error. Check if the Python backend is running.")
        } catch (e: HttpException) {
            throw Exception("Server error (${e.code()}). The Trust Engine encountered an issue.")
        } catch (e: Exception) {
            throw Exception(e.message ?: "An unexpected error occurred while analyzing evidence.")
        }
        
        val riskLvl = mapRiskLevel(response.classification)

        // Android should not invent risk heuristics. Threat detected if risk is not LOW.
        val threatDetected = riskLvl != RiskLevel.LOW

        return TrustAssessment(
            id = UUID.randomUUID().toString(),
            contextId = context.id,
            riskScore = response.riskScore?.toFloat() ?: 0f,
            riskLevel = riskLvl,
            summary = response.explanation ?: "No summary provided.",
            signals = response.threats ?: emptyList(),
            recommendation = response.recommendedAction ?: "No recommendation provided.",
            isThreatDetected = threatDetected,
            title = if (threatDetected) "Suspicious Content Detected" else "Content Verified",
            confidence = null, // Backend does not provide confidence in this schema
            timestamp = System.currentTimeMillis()
        )
    }

    private fun mapRiskLevel(classification: String?): RiskLevel {
        if (classification == null) return RiskLevel.LOW
        
        return when (classification.uppercase()) {
            "CRITICAL", "CRITICAL_RISK", "SEVERE" -> RiskLevel.CRITICAL
            "HIGH", "HIGH_RISK" -> RiskLevel.HIGH
            "MEDIUM", "MODERATE", "MODERATE_RISK", "SUSPICIOUS" -> RiskLevel.MEDIUM
            "LOW", "SAFE", "SECURE", "LOW_RISK" -> RiskLevel.LOW
            else -> RiskLevel.MEDIUM // Fallback to medium for unknown classifications for safety
        }
    }
}
