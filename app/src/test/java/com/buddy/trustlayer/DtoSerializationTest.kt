package com.buddy.trustlayer

import com.buddy.trustlayer.data.remote.BuddyResponseDto
import com.buddy.trustlayer.data.remote.TextAnalysisRequestDto
import com.buddy.trustlayer.data.remote.UrlAnalysisRequestDto
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class DtoSerializationTest {
    private val gson = Gson()

    @Test
    fun `test TextAnalysisRequestDto serialization`() {
        val dto = TextAnalysisRequestDto(text = "Hello world")
        val json = gson.toJson(dto)
        assertEquals("{\"text\":\"Hello world\"}", json)
    }

    @Test
    fun `test UrlAnalysisRequestDto serialization`() {
        val dto = UrlAnalysisRequestDto(url = "https://example.com")
        val json = gson.toJson(dto)
        assertEquals("{\"url\":\"https://example.com\"}", json)
    }

    @Test
    fun `test BuddyResponseDto deserialization`() {
        val json = """
            {
                "risk_score": 80,
                "classification": "HIGH_RISK",
                "threats": ["Urgency", "Credential request"],
                "explanation": "Suspicious login request",
                "recommended_action": "Do not click"
            }
        """.trimIndent()

        val dto = gson.fromJson(json, BuddyResponseDto::class.java)

        assertEquals(80, dto.riskScore)
        assertEquals("HIGH_RISK", dto.classification)
        assertEquals(listOf("Urgency", "Credential request"), dto.threats)
        assertEquals("Suspicious login request", dto.explanation)
        assertEquals("Do not click", dto.recommendedAction)
    }
    
    @Test
    fun `test BuddyResponseDto with safe missing fields`() {
        val json = "{}"
        val dto = gson.fromJson(json, BuddyResponseDto::class.java)
        
        assertEquals(null, dto.riskScore)
        assertEquals(null, dto.classification)
        assertEquals(null, dto.threats)
        assertEquals(null, dto.explanation)
        assertEquals(null, dto.recommendedAction)
    }
}
