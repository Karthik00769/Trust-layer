package com.buddy.trustlayer.feature.assessment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.buddy.trustlayer.data.repository.InMemoryHistoryRepository
import com.buddy.trustlayer.domain.model.RiskLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    assessmentId: String,
    onNavigateToVerification: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val assessment = InMemoryHistoryRepository.getAssessment(assessmentId)

    if (assessment == null) {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Assessment not found", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateHome) {
                    Text("Return to Home")
                }
            }
        }
        return
    }

    val riskColor = when (assessment.riskLevel) {
        RiskLevel.LOW -> MaterialTheme.colorScheme.primary
        RiskLevel.MEDIUM -> MaterialTheme.colorScheme.tertiary 
        RiskLevel.HIGH, RiskLevel.CRITICAL -> MaterialTheme.colorScheme.error
    }

    val isHighOrCritical = assessment.riskLevel == RiskLevel.HIGH || assessment.riskLevel == RiskLevel.CRITICAL

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assessment Result") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("< Back")
                    }
                },
                actions = {
                    TextButton(onClick = onNavigateHome) {
                        Text("Home")
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Prominent Risk Banner
            Surface(
                color = riskColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (assessment.riskLevel) {
                            RiskLevel.HIGH, RiskLevel.CRITICAL -> "🛑 STOP — HIGH RISK THREAT"
                            RiskLevel.MEDIUM -> "⚠️ SUSPICIOUS — EXERCISE CAUTION"
                            RiskLevel.LOW -> "✅ VERIFIED SAFE"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = riskColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Risk Score: ${assessment.riskScore.toInt()}/100",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    assessment.confidence?.let { conf ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Model Confidence: ${(conf * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = assessment.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (assessment.signals.isNotEmpty()) {
                Text(
                    text = "Detected Signals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                assessment.signals.forEach { signal ->
                    Text(
                        text = "• $signal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Recommendation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = assessment.recommendation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Primary & Secondary Navigation Actions
            if (isHighOrCritical || assessment.riskLevel == RiskLevel.MEDIUM) {
                Button(
                    onClick = { onNavigateToVerification(assessmentId) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = riskColor)
                ) {
                    Text("Continue to Verification", fontWeight = FontWeight.Bold)
                }
            }

            OutlinedButton(
                onClick = onNavigateHome,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Return to Home", fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = onNavigateToHistory,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("View Session History", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
