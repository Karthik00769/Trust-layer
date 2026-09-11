package com.buddy.trustlayer.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface TrustEngineApi {
    @POST("analyze/text")
    suspend fun analyzeText(@Body request: TextAnalysisRequestDto): BuddyResponseDto

    @POST("analyze/url")
    suspend fun analyzeUrl(@Body request: UrlAnalysisRequestDto): BuddyResponseDto
}
