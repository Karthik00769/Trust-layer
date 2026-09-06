package com.buddy.trustlayer.domain.model

import java.util.UUID

/**
 * Represents the relevant context prepared from user evidence before assessment.
 */
data class TrustContext(
    val id: String = UUID.randomUUID().toString(),
    val evidenceId: String = "",
    val content: String,
    val url: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
