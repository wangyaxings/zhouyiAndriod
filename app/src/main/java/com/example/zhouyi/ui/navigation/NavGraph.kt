package com.example.zhouyi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zhouyi.ui.screens.home.HomeScreen
import com.example.zhouyi.ui.screens.quiz.QuizScreen
import com.example.zhouyi.ui.screens.wrongbook.WrongBookScreen
import com.example.zhouyi.ui.screens.statistics.StatisticsScreen
import com.example.zhouyi.ui.screens.settings.SettingsScreen

/**
 * 应用导航图
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToQuiz = { mode ->
                    navController.navigate(Screen.Quiz.createRoute(mode))
                },
                onNavigateToWrongBook = {
                    navController.navigate(Screen.WrongBook.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = Screen.Quiz.arguments
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "practice"
            QuizScreen(
                mode = mode,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.WrongBook.route) {
            WrongBookScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToQuiz = { mode ->
                    navController.navigate(Screen.Quiz.createRoute(mode))
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * 屏幕路由定义
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quiz : Screen("quiz/{mode}") {
        val arguments = listOf(
            androidx.navigation.NavArgument.Builder()
                .setType(androidx.navigation.NavType.StringType)
                .setDefaultValue("practice")
                .setIsNullable(false)
                .build()
        )

        fun createRoute(mode: String) = "quiz/$mode"
    }
    object WrongBook : Screen("wrongbook")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
}
