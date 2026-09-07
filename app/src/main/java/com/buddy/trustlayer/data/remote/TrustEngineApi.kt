package com.buddy.trustlayer.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface TrustEngineApi {
    @POST("api/v1/assess")
    suspend fun assessEvidence(@Body request: AssessmentRequestDto): AssessmentResponseDto
}
