package com.buddy.trustlayer.feature.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.buddy.trustlayer.data.repository.InMemoryContextRepository
import com.buddy.trustlayer.domain.model.TrustContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceScreen(
    onNavigateToEngine: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var content by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evidence Capture") },
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
                text = "Provide content for analysis",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedTextField(
                value = content,
                onValueChange = { 
                    content = it
                    showError = false 
                },
                label = { Text("Message or suspicious text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap to attach file or image (Placeholder)",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (showError) {
                Text(
                    text = "Please provide some text or a URL to analyze.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (content.isBlank() && url.isBlank()) {
                        showError = true
                    } else {
                        val trustContext = TrustContext(content = content, url = url)
                        InMemoryContextRepository.saveContext(trustContext)
                        onNavigateToEngine(trustContext.id)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Analyze Evidence", fontWeight = FontWeight.Bold)
            }
        }
    }
}
