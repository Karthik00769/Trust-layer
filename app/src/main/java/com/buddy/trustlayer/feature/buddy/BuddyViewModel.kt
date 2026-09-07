package com.buddy.trustlayer.feature.buddy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buddy.trustlayer.data.repository.InMemoryHistoryRepository
import com.buddy.trustlayer.domain.model.TrustAssessment
import com.buddy.trustlayer.domain.model.TrustContext
import com.buddy.trustlayer.domain.repository.TrustEngineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val assessment: TrustAssessment? = null
)

class BuddyViewModel(
    private val repository: TrustEngineRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun sendMessage(text: String) {
        val userMsg = ChatMessage(text = text, isUser = true)
        _messages.value = _messages.value + userMsg

        _isProcessing.value = true

        viewModelScope.launch {
            try {
                val context = TrustContext(content = text)
                val assessment = repository.assess(context)
                
                InMemoryHistoryRepository.addAssessment(assessment)

                val responseText = "Here is my assessment of that content."
                val buddyMsg = ChatMessage(
                    text = responseText, 
                    isUser = false, 
                    assessment = assessment
                )
                
                _messages.value = _messages.value + buddyMsg
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    text = "Sorry, I encountered an error analyzing that.",
                    isUser = false
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }
}
