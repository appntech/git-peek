package com.appntech.gitpeek.explore.ui.navigation

import GitHubUserDetailScreen
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appntech.gitpeek.explore.ui.GitHubUserListScreen


@Composable
fun GitPeekNavGraph()
{
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "user_list",
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable("user_list") {
            GitHubUserListScreen(navController = navController)
        }
        composable(
            route = "user_detail/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            GitHubUserDetailScreen(username, navController)
        }
    }
}