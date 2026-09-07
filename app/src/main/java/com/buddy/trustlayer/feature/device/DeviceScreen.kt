package com.buddy.trustlayer.feature.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.buddy.trustlayer.core.common.AppConfig
import com.buddy.trustlayer.data.repository.InMemoryHistoryRepository
import com.buddy.trustlayer.domain.model.RiskLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    onNavigateBack: () -> Unit
) {
    val assessments by InMemoryHistoryRepository.assessments.collectAsState()
    val highRiskCount = assessments.count { it.riskLevel == RiskLevel.HIGH || it.riskLevel == RiskLevel.CRITICAL }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Status") },
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
            
            // Engine Config Card
            var useMockEngine by remember { mutableStateOf(AppConfig.USE_MOCK_ENGINE) }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Backend Connection Mode",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (useMockEngine) "Local Mock Engine" else "Remote Python API",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (useMockEngine) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        )
                    }
                    Switch(
                        checked = !useMockEngine,
                        onCheckedChange = { isRemote ->
                            useMockEngine = !isRemote
                            AppConfig.USE_MOCK_ENGINE = !isRemote
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "API Base URL: ${AppConfig.BACKEND_BASE_URL}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Assessment Stats Card
            DeviceStatusCard(
                title = "Session Activity",
                value = "${assessments.size} Assessments",
                subtitle = "$highRiskCount high-risk threats detected",
                statusColor = if (highRiskCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            
            // App Protection
            DeviceStatusCard(
                title = "App Protection",
                value = "Active",
                subtitle = "Trust Layer is monitoring user-submitted context.",
                statusColor = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Device-level analytics and auto-scanning require additional Android permissions not yet requested.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DeviceStatusCard(
    title: String,
    value: String,
    subtitle: String,
    statusColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
