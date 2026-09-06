package com.buddy.trustlayer.feature.engine

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.buddy.trustlayer.data.repository.InMemoryContextRepository
import com.buddy.trustlayer.data.repository.InMemoryHistoryRepository
import com.buddy.trustlayer.data.repository.MockTrustEngineRepository
import kotlinx.coroutines.delay

@Composable
fun EngineScreen(
    contextId: String,
    onAssessmentComplete: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var currentStage by remember { mutableStateOf("Collecting evidence...") }
    
    LaunchedEffect(contextId) {
        val context = InMemoryContextRepository.getContext(contextId)
        if (context != null) {
            delay(800)
            currentStage = "Inspecting signals..."
            delay(800)
            currentStage = "Assessing risk..."
            delay(800)
            currentStage = "Preparing recommendation..."
            
            val assessment = MockTrustEngineRepository().assess(context)
            InMemoryHistoryRepository.addAssessment(assessment)
            onAssessmentComplete(assessment.id)
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp),
                strokeWidth = 6.dp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = currentStage,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Please wait while Trust Layer analyzes the content.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
