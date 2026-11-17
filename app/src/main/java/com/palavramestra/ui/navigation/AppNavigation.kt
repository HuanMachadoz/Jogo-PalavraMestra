package com.palavramestra.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.palavramestra.ui.screens.admin.AdminScreen
import com.palavramestra.ui.screens.admin_edit.AdminEditScreen
import com.palavramestra.ui.screens.game.GameScreen
import com.palavramestra.ui.screens.login.LoginScreen
import com.palavramestra.ui.screens.ranking.RankingScreen
import com.palavramestra.ui.screens.settings.SettingsScreen
import com.palavramestra.ui.viewmodel.Difficulty

private data class BottomItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val playerBottomItems = listOf(
    BottomItem(Routes.GAME, "Jogar", Icons.Default.PlayArrow),
    BottomItem(Routes.SETTINGS, "Config", Icons.Default.Settings),
    BottomItem(Routes.RANKING, "Ranking", Icons.Default.BarChart)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    var playerName by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIO) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(Routes.GAME, Routes.SETTINGS, Routes.RANKING)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                PlayerBottomBar(currentRoute) { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.GAME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginPlayer = { name ->
                        playerName = name
                        navController.navigate(Routes.GAME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onLoginAdmin = {
                        navController.navigate(Routes.ADMIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.GAME) {
                GameScreen(
                    playerName = playerName,
                    onGoToSettings = { navController.navigate(Routes.SETTINGS) },
                    onGoToRanking = { navController.navigate(Routes.RANKING) },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    selectedDifficulty = selectedDifficulty,
                    onDifficultyChange = { selectedDifficulty = it },
                    onBackToGame = { navController.navigate(Routes.GAME) }
                )
            }

            composable(Routes.RANKING) {
                RankingScreen(
                    onBackToGame = { navController.navigate(Routes.GAME) }
                )
            }

            composable(Routes.ADMIN) {
                AdminScreen(
                    onAddWord = { navController.navigate(Routes.ADMIN_EDIT) },
                    onEditWord = { id ->
                        navController.navigate("${Routes.ADMIN_EDIT}/$id")
                    },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.ADMIN_EDIT) {
                AdminEditScreen(
                    wordId = null,
                    onSaved = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = "${Routes.ADMIN_EDIT}/{wordId}",
                arguments = listOf(
                    navArgument("wordId") { type = NavType.IntType }
                )
            ) { entry ->
                val wordId = entry.arguments?.getInt("wordId")
                AdminEditScreen(
                    wordId = wordId,
                    onSaved = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun PlayerBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar {
        playerBottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
