package com.buddy.trustlayer.data.remote

import com.google.gson.annotations.SerializedName

data class TextAnalysisRequestDto(
    @SerializedName("text") val text: String
)

data class UrlAnalysisRequestDto(
    @SerializedName("url") val url: String
)

data class BuddyResponseDto(
    @SerializedName("risk_score") val riskScore: Int?,
    @SerializedName("classification") val classification: String?,
    @SerializedName("threats") val threats: List<String>?,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("recommended_action") val recommendedAction: String?,
    @SerializedName("rag_knowledge") val ragKnowledge: List<Map<String, Any>>? = null
)
