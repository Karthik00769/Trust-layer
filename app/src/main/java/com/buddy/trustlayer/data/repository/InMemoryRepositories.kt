package com.buddy.trustlayer.data.repository

import android.content.Context
import com.buddy.trustlayer.data.local.LocalHistoryRepository
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
    private var localRepository: LocalHistoryRepository? = null
    private val _assessments = MutableStateFlow<List<TrustAssessment>>(emptyList())
    val assessments: StateFlow<List<TrustAssessment>>
        get() = localRepository?.assessments ?: _assessments.asStateFlow()

    fun init(context: Context) {
        if (localRepository == null) {
            localRepository = LocalHistoryRepository(context.applicationContext)
        }
    }

    fun addAssessment(assessment: TrustAssessment) {
        val repo = localRepository
        if (repo != null) {
            repo.addAssessment(assessment)
        } else {
            _assessments.value = listOf(assessment) + _assessments.value.filter { it.id != assessment.id }
        }
    }
    
    fun getAssessment(id: String): TrustAssessment? {
        return localRepository?.getAssessment(id) ?: _assessments.value.find { it.id == id }
    }
}
