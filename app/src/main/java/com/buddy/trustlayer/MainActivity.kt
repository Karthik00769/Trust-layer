package com.buddy.trustlayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.buddy.trustlayer.core.navigation.TrustLayerApp
import com.buddy.trustlayer.ui.theme.BuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuddyTheme {
                TrustLayerApp()
            }
        }
    }
}