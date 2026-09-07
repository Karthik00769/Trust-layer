package com.buddy.trustlayer.data.repository

import com.buddy.trustlayer.data.remote.AssessmentRequestDto
import com.buddy.trustlayer.data.remote.TrustEngineApi
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
        val request = AssessmentRequestDto(
            content = context.content,
            source = context.source ?: context.url.takeIf { it.isNotBlank() },
            evidenceType = context.evidenceType,
            deviceContext = context.deviceContext
        )
        
        val response = try {
            api.assessEvidence(request)
        } catch (e: IOException) {
            throw Exception("Network connection error. Check if the Python backend is running.")
        } catch (e: HttpException) {
            throw Exception("Server error (${e.code()}). The Trust Engine encountered an issue.")
        } catch (e: Exception) {
            throw Exception(e.message ?: "An unexpected error occurred while analyzing evidence.")
        }
        
        val riskLvl = try {
            RiskLevel.valueOf((response.riskLevel ?: "LOW").uppercase())
        } catch (e: Exception) {
            RiskLevel.MEDIUM
        }

        val threatDetected = response.threatDetected ?: false

        return TrustAssessment(
            id = response.id ?: UUID.randomUUID().toString(),
            contextId = context.id,
            riskScore = response.riskScore ?: 0f,
            riskLevel = riskLvl,
            summary = response.summary ?: "No summary provided.",
            signals = response.indicators ?: emptyList(),
            recommendation = response.recommendation ?: "No recommendation provided.",
            isThreatDetected = threatDetected,
            title = if (threatDetected) "Suspicious Content Detected" else "Content Verified",
            confidence = response.confidence,
            timestamp = response.timestamp ?: System.currentTimeMillis()
        )
    }
}
