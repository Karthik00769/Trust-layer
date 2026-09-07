package com.buddy.trustlayer.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.buddy.trustlayer.feature.buddy.BuddyViewModel
import com.buddy.trustlayer.feature.engine.EngineViewModel

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EngineViewModel::class.java)) {
            return EngineViewModel(AppContainer.trustEngineRepository) as T
        }
        if (modelClass.isAssignableFrom(BuddyViewModel::class.java)) {
            return BuddyViewModel(AppContainer.trustEngineRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
