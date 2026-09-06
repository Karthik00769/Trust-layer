package com.buddy.trustlayer.domain.repository

import com.buddy.trustlayer.domain.model.TrustAssessment
import com.buddy.trustlayer.domain.model.TrustContext

/**
 * Interface through which the Android application requests an assessment.
 */
interface TrustEngineRepository {
    suspend fun assess(context: TrustContext): TrustAssessment
}
