package com.hussein.pdfreader.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hussein.pdfreader.feature.history.HistoryScreen
import com.hussein.pdfreader.feature.pdf.PdfScreen

sealed class Screen(val route: String) {
    object Pdf : Screen("pdf?uri={uri}") {
        fun passUri(uri: String?) = "pdf?uri=$uri"
    }
    object History : Screen("history")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    initialUri: Uri?
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Pdf.route
    ) {
        composable(
            route = Screen.Pdf.route,
            arguments = listOf(navArgument("uri") { nullable = true })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uri")
            val uri = uriString?.let { Uri.parse(it) } ?: initialUri
            PdfScreen(
                initialUri = uri,
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }
        composable(route = Screen.History.route) {
            HistoryScreen(
                onNavigateToPdf = { uri ->
                    navController.navigate(Screen.Pdf.passUri(Uri.encode(uri)))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}