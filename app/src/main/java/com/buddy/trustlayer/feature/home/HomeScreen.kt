package com.buddy.trustlayer.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEvidence: () -> Unit,
    onNavigateToBuddy: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDevice: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trust Layer") },
                actions = {
                    TextButton(onClick = onNavigateToDevice) {
                        Text("Device")
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
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Your AI-powered financial safety system. Verify context, evaluate threats, and protect your digital identity.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Primary Actions
            Button(
                onClick = onNavigateToEvidence,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Check Something", fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = onNavigateToBuddy,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("Ask Trust Buddy", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))
            
            TextButton(
                onClick = onNavigateToHistory,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("View History", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
