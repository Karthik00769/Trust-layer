package com.buddy.trustlayer.feature.engine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buddy.trustlayer.data.repository.InMemoryContextRepository
import com.buddy.trustlayer.data.repository.InMemoryHistoryRepository
import com.buddy.trustlayer.domain.repository.TrustEngineRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EngineUiState {
    object Idle : EngineUiState()
    data class Processing(val stage: String) : EngineUiState()
    data class Success(val assessmentId: String) : EngineUiState()
    data class Error(val message: String) : EngineUiState()
}

class EngineViewModel(
    private val repository: TrustEngineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EngineUiState>(EngineUiState.Idle)
    val uiState: StateFlow<EngineUiState> = _uiState.asStateFlow()

    fun processContext(contextId: String) {
        if (_uiState.value !is EngineUiState.Idle) return

        val context = InMemoryContextRepository.getContext(contextId)
        if (context == null) {
            _uiState.value = EngineUiState.Error("Context not found")
            return
        }

        viewModelScope.launch {
            try {
                // Visual pipeline simulation
                _uiState.value = EngineUiState.Processing("Extracting signals...")
                delay(700)
                _uiState.value = EngineUiState.Processing("Analyzing risk...")
                delay(700)
                _uiState.value = EngineUiState.Processing("Generating assessment...")
                delay(700)
                
                // Actual assessment call (Mock or Remote based on AppContainer)
                val assessment = repository.assess(context)
                
                InMemoryHistoryRepository.addAssessment(assessment)
                _uiState.value = EngineUiState.Success(assessment.id)
            } catch (e: Exception) {
                _uiState.value = EngineUiState.Error(e.message ?: "Analysis failed")
            }
        }
    }
}
