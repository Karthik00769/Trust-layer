package com.buddy.trustlayer.feature.engine

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buddy.trustlayer.core.common.AppConfig
import com.buddy.trustlayer.core.common.ViewModelFactory

@Composable
fun EngineScreen(
    contextId: String,
    onAssessmentComplete: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EngineViewModel = viewModel(factory = ViewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(contextId) {
        viewModel.processContext(contextId)
    }

    LaunchedEffect(uiState) {
        if (uiState is EngineUiState.Success) {
            onAssessmentComplete((uiState as EngineUiState.Success).assessmentId)
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
            when (val state = uiState) {
                is EngineUiState.Idle, is EngineUiState.Processing -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 6.dp
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = if (state is EngineUiState.Processing) state.stage else "Initializing...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Trust Engine is analyzing the evidence.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = if (AppConfig.USE_MOCK_ENGINE) "Mode: Local Mock Engine" else "Mode: Remote API Backend",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is EngineUiState.Error -> {
                    Text(
                        text = "⚠️",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Analysis Failed",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onNavigateBack) {
                            Text("Try Again")
                        }
                        Button(onClick = onNavigateHome) {
                            Text("Return Home")
                        }
                    }
                }
                is EngineUiState.Success -> {
                    // Handled by LaunchedEffect
                }
            }
        }
    }
}
