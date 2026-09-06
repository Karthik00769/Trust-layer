package com.buddy.trustlayer.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.buddy.trustlayer.feature.assessment.AssessmentScreen
import com.buddy.trustlayer.feature.buddy.BuddyScreen
import com.buddy.trustlayer.feature.device.DeviceScreen
import com.buddy.trustlayer.feature.engine.EngineScreen
import com.buddy.trustlayer.feature.evidence.EvidenceScreen
import com.buddy.trustlayer.feature.history.HistoryScreen
import com.buddy.trustlayer.feature.home.HomeScreen
import com.buddy.trustlayer.feature.verification.VerificationScreen

@Composable
fun TrustLayerApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToEvidence = { navController.navigate(Screen.Evidence.route) },
                    onNavigateToBuddy = { navController.navigate(Screen.Buddy.route) },
                    onNavigateToHistory = {
                        navController.navigate(Screen.History.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Buddy.route) {
                BuddyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToAssessment = { assessmentId ->
                        navController.navigate(Screen.Assessment.createRoute(assessmentId))
                    },
                    onNavigateBack = { 
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            
            composable(Screen.Evidence.route) {
                EvidenceScreen(
                    onNavigateToEngine = { contextId ->
                        navController.navigate(Screen.Engine.createRoute(contextId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.Engine.route,
                arguments = listOf(navArgument("contextId") { type = NavType.StringType })
            ) { backStackEntry ->
                val contextId = backStackEntry.arguments?.getString("contextId") ?: ""
                EngineScreen(
                    contextId = contextId,
                    onAssessmentComplete = { assessmentId ->
                        // Navigate to Assessment, clear Engine from backstack
                        navController.navigate(Screen.Assessment.createRoute(assessmentId)) {
                            popUpTo(Screen.Evidence.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.Assessment.route,
                arguments = listOf(navArgument("assessmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val assessmentId = backStackEntry.arguments?.getString("assessmentId") ?: ""
                AssessmentScreen(
                    assessmentId = assessmentId,
                    onNavigateToVerification = { id ->
                        navController.navigate(Screen.Verification.createRoute(id))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.Verification.route,
                arguments = listOf(navArgument("assessmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val assessmentId = backStackEntry.arguments?.getString("assessmentId") ?: ""
                VerificationScreen(
                    assessmentId = assessmentId,
                    onComplete = {
                        // Navigate to History, clear workflow stack
                        navController.navigate(Screen.History.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Device.route) {
                DeviceScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Buddy,
        Screen.History
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Only show bottom nav if we are on one of the top-level routes
    val showBottomNav = items.any { it.route == currentDestination?.route }
    
    if (showBottomNav) {
        NavigationBar {
            items.forEach { screen ->
                NavigationBarItem(
                    icon = { Text(screen.route.replaceFirstChar { it.uppercase() }.take(1)) },
                    label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
