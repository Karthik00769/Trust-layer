package com.buddy.trustlayer.feature.evidence

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.buddy.trustlayer.data.repository.InMemoryContextRepository
import com.buddy.trustlayer.domain.model.TrustContext
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceScreen(
    onNavigateToEngine: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var content by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var evidenceType by remember { mutableStateOf("TEXT") }
    var isProcessingImage by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Image Picker for Screenshot (OCR)
    val ocrPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isProcessingImage = true
            statusMessage = "Extracting text from image..."
            try {
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        isProcessingImage = false
                        if (visionText.text.isNotBlank()) {
                            content = visionText.text
                            statusMessage = "Text extracted from screenshot!"
                        } else {
                            statusMessage = "No readable text found in image."
                        }
                    }
                    .addOnFailureListener {
                        isProcessingImage = false
                        statusMessage = "OCR processing failed."
                    }
            } catch (e: Exception) {
                isProcessingImage = false
                statusMessage = "Failed to load image."
            }
        }
    }

    // Image Picker for QR Code Decoding
    val qrPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isProcessingImage = true
            statusMessage = "Decoding QR code..."
            try {
                val image = InputImage.fromFilePath(context, uri)
                val scanner = BarcodeScanning.getClient()
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        isProcessingImage = false
                        val qrPayload = barcodes.firstOrNull()?.rawValue
                        if (!qrPayload.isNullOrBlank()) {
                            if (qrPayload.startsWith("http://", ignoreCase = true) || qrPayload.startsWith("https://", ignoreCase = true)) {
                                url = qrPayload
                                evidenceType = "URL"
                                statusMessage = "Decoded QR Link: $qrPayload"
                            } else {
                                content = qrPayload
                                evidenceType = "TEXT"
                                statusMessage = "Decoded QR Content: $qrPayload"
                            }
                        } else {
                            statusMessage = "No QR code detected in image."
                        }
                    }
                    .addOnFailureListener {
                        isProcessingImage = false
                        statusMessage = "QR scan failed."
                    }
            } catch (e: Exception) {
                isProcessingImage = false
                statusMessage = "Failed to load QR image."
            }
        }
    }

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

            // Evidence Action Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { ocrPickerLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("📷 OCR", style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    onClick = { qrPickerLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("🔳 Scan QR", style = MaterialTheme.typography.bodySmall)
                }
            }

            statusMessage?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = content,
                onValueChange = { 
                    content = it
                    showError = false 
                },
                label = { Text("Message or suspicious text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = url,
                onValueChange = { 
                    url = it
                    if (it.isNotBlank()) evidenceType = "URL"
                },
                label = { Text("URL (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            if (isProcessingImage) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
                        val trustContext = TrustContext(
                            content = content, 
                            url = url,
                            evidenceType = if (url.isNotBlank() && content.isBlank()) "URL" else evidenceType
                        )
                        InMemoryContextRepository.saveContext(trustContext)
                        onNavigateToEngine(trustContext.id)
                    }
                },
                enabled = !isProcessingImage,
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
