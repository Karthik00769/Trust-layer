package com.buddy.trustlayer.core.navigation

sealed class Screen(val route: String) {
    // Bottom Nav Destinations
    object Home : Screen("home")
    object Buddy : Screen("buddy")
    object History : Screen("history")

    // Other Destinations
    object Evidence : Screen("evidence")
    object Engine : Screen("engine/{contextId}") {
        fun createRoute(contextId: String) = "engine/$contextId"
    }
    object Assessment : Screen("assessment/{assessmentId}") {
        fun createRoute(assessmentId: String) = "assessment/$assessmentId"
    }
    object Verification : Screen("verification/{assessmentId}") {
        fun createRoute(assessmentId: String) = "verification/$assessmentId"
    }
    object Device : Screen("device")
}
