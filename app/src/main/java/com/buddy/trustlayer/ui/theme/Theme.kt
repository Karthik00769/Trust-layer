package com.buddy.trustlayer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val TrustLayerColorScheme = darkColorScheme(
    primary = TealMintPrimary,
    secondary = TealMintVariant,
    background = NearBlack,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceElevated,
    onPrimary = NearBlack,
    onSecondary = NearBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = RiskRed,
    onError = TextPrimary
)

@Composable
fun BuddyTheme(
    // Trust Layer is dark mode only by design
    darkTheme: Boolean = true,
    // Disable dynamic color for brand consistency
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = TrustLayerColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
