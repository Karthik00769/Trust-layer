package com.buddy.trustlayer.data.repository

import com.buddy.trustlayer.domain.model.TrustAssessment
import com.buddy.trustlayer.domain.model.TrustContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object InMemoryContextRepository {
    private val contexts = mutableMapOf<String, TrustContext>()
    
    fun saveContext(context: TrustContext) {
        contexts[context.id] = context
    }
    
    fun getContext(id: String): TrustContext? {
        return contexts[id]
    }
}

object InMemoryHistoryRepository {
    private val _assessments = MutableStateFlow<List<TrustAssessment>>(emptyList())
    val assessments: StateFlow<List<TrustAssessment>> = _assessments.asStateFlow()

    fun addAssessment(assessment: TrustAssessment) {
        _assessments.value = listOf(assessment) + _assessments.value
    }
    
    fun getAssessment(id: String): TrustAssessment? {
        return _assessments.value.find { it.id == id }
    }
}
