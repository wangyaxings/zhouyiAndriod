package com.example.zhouyi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.zhouyi.ui.screens.home.HomeScreen
import com.example.zhouyi.ui.screens.quiz.QuizScreen
import com.example.zhouyi.ui.screens.wrongbook.WrongBookScreen
import com.example.zhouyi.ui.screens.statistics.StatisticsScreen
import com.example.zhouyi.ui.screens.settings.SettingsScreen
import com.example.zhouyi.ui.screens.checkin.CheckInScreen
import com.example.zhouyi.ui.screens.home.HomeViewModel
import com.example.zhouyi.ui.screens.quiz.QuizViewModel
import com.example.zhouyi.ui.screens.wrongbook.WrongBookViewModel
import com.example.zhouyi.ui.screens.statistics.StatisticsViewModel
import com.example.zhouyi.ui.screens.settings.SettingsViewModel
import com.example.zhouyi.ui.screens.checkin.CheckInViewModel
import com.example.zhouyi.data.repository.*
import com.example.zhouyi.data.preferences.AppPreferences
import com.example.zhouyi.core.di.AppModule

/**
 * 应用导航图
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    val context = LocalContext.current
    val database = remember { AppModule.provideDatabase(context) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            val vm = remember {
                HomeViewModel(
                    attemptRepository = AppModule.provideAttemptRepository(context),
                    wrongBookRepository = AppModule.provideWrongBookRepository(context),
                    srsRepository = AppModule.provideSrsRepository(context),
                    preferences = AppModule.provideAppPreferences(context)
                )
            }
            HomeScreen(
                viewModel = vm,
                onNavigateToQuiz = { navController.navigate(Screen.Quiz.createRoute("practice")) },
                onNavigateToWrongBook = { navController.navigate(Screen.WrongBook.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToCheckIn = { navController.navigate(Screen.CheckIn.route) }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = Screen.Quiz.arguments
        ) {
            val vm = remember {
                QuizViewModel(
                    hexagramRepository = AppModule.provideHexagramRepository(context),
                    attemptRepository = AppModule.provideAttemptRepository(context),
                    wrongBookRepository = AppModule.provideWrongBookRepository(context),
                    srsRepository = AppModule.provideSrsRepository(context),
                    checkInRepository = AppModule.provideCheckInRepository(database),
                    preferences = AppModule.provideAppPreferences(context)
                )
            }
            QuizScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCalendar = { navController.navigate(Screen.CheckIn.route) }
            )
        }

        composable(Screen.WrongBook.route) {
            val vm = remember {
                WrongBookViewModel(
                    wrongBookRepository = AppModule.provideWrongBookRepository(context),
                    hexagramRepository = AppModule.provideHexagramRepository(context)
                )
            }
            WrongBookScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onStartReview = { navController.navigate(Screen.Quiz.createRoute("practice")) }
            )
        }

        composable(Screen.Statistics.route) {
            val vm = remember {
                StatisticsViewModel(
                    attemptRepository = AppModule.provideAttemptRepository(context),
                    srsRepository = AppModule.provideSrsRepository(context),
                    wrongBookRepository = AppModule.provideWrongBookRepository(context)
                )
            }
            StatisticsScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val vm = remember { SettingsViewModel(application = (context.applicationContext as android.app.Application)) }
            SettingsScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CheckIn.route) {
            val vm = remember {
                CheckInViewModel(
                    checkInRepository = AppModule.provideCheckInRepository(database)
                )
            }
            CheckInScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
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
            navArgument("mode") {
                type = NavType.StringType
                defaultValue = "practice"
            }
        )

        fun createRoute(mode: String) = "quiz/$mode"
    }
    object WrongBook : Screen("wrongbook")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object CheckIn : Screen("checkin")
}
