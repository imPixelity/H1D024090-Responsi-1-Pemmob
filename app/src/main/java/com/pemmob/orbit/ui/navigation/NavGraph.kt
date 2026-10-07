package com.pemmob.orbit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pemmob.orbit.ui.detail.DetailScreen
import com.pemmob.orbit.ui.detail.DetailViewModel
import com.pemmob.orbit.ui.home.HomeScreen
import com.pemmob.orbit.ui.home.HomeViewModel

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{malId}"
    fun detail(malId: Int) = "detail/$malId"
}

@Composable
fun OrbitNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = viewModel,
                onAnimeClick = { malId ->
                    navController.navigate(Routes.detail(malId))
                }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("malId") { type = NavType.IntType })
        ) { backStackEntry ->
            val malId = backStackEntry.arguments?.getInt("malId") ?: 0
            val viewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.Factory(malId)
            )
            DetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
