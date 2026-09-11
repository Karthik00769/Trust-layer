package com.buddy.trustlayer.data.local

import android.content.Context
import com.buddy.trustlayer.domain.model.TrustAssessment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class LocalHistoryRepository(private val context: Context) {
    private val gson = Gson()
    private val historyFile: File by lazy { File(context.filesDir, "trust_history.json") }

    private val _assessments = MutableStateFlow<List<TrustAssessment>>(emptyList())
    val assessments: StateFlow<List<TrustAssessment>> = _assessments.asStateFlow()

    init {
        _assessments.value = loadAssessments()
    }

    private fun loadAssessments(): List<TrustAssessment> {
        return try {
            if (historyFile.exists()) {
                val json = historyFile.readText()
                val type = object : TypeToken<List<TrustAssessment>>() {}.type
                gson.fromJson<List<TrustAssessment>>(json, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addAssessment(assessment: TrustAssessment) {
        val updated = listOf(assessment) + _assessments.value.filter { it.id != assessment.id }
        _assessments.value = updated
        saveAssessments(updated)
    }

    private fun saveAssessments(list: List<TrustAssessment>) {
        try {
            val json = gson.toJson(list)
            historyFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAssessment(id: String): TrustAssessment? {
        return _assessments.value.find { it.id == id }
    }
}
