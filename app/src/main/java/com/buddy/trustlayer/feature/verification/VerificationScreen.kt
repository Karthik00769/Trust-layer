package com.buddy.trustlayer.feature.verification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.buddy.trustlayer.data.repository.InMemoryHistoryRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    assessmentId: String,
    onComplete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val assessment = InMemoryHistoryRepository.getAssessment(assessmentId)
    
    var step1Checked by remember { mutableStateOf(false) }
    var step2Checked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Action Required") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("< Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Verification Checklist",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Please complete the following actions to secure your context.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = step1Checked,
                    onCheckedChange = { step1Checked = it }
                )
                Text(
                    text = "I have reviewed the assessment details",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = step2Checked,
                    onCheckedChange = { step2Checked = it }
                )
                Text(
                    text = if (assessment?.isThreatDetected == true) 
                        "I have blocked the sender and ignored any links" 
                    else 
                        "I have verified the sender's identity",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = step1Checked && step2Checked,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Complete & Save", fontWeight = FontWeight.Bold)
            }
        }
    }
}
