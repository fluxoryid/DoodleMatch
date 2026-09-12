package com.fluxoryid.doodlematch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fluxoryid.doodlematch.ui.screens.DrawScreen
import com.fluxoryid.doodlematch.ui.screens.HomeScreen
import com.fluxoryid.doodlematch.ui.screens.PlayMode
import com.fluxoryid.doodlematch.ui.screens.match.MatchScreen
import com.fluxoryid.doodlematch.ui.screens.parent.ParentZoneScreen
import com.fluxoryid.doodlematch.ui.screens.stickers.StickersScreen

private object Routes {
    const val HOME = "home"
    const val DRAW = "draw/{categoryId}"
    const val MATCH = "match/{categoryId}"
    const val STICKERS = "stickers"
    const val PARENT = "parent"
}

@Composable
fun DoodleMatchNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onCategorySelected = { categoryId, mode ->
                    val route = when (mode) {
                        PlayMode.DRAW -> "draw/$categoryId"
                        PlayMode.MATCH -> "match/$categoryId"
                    }
                    navController.navigate(route)
                },
                onStickers = { navController.navigate(Routes.STICKERS) },
                onParentZone = { navController.navigate(Routes.PARENT) },
            )
        }

        composable(
            route = Routes.DRAW,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId").orEmpty()
            DrawScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.MATCH,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId").orEmpty()
            MatchScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onGoToDraw = { navController.navigate("draw/$categoryId") },
                onOpenStickers = { navController.navigate(Routes.STICKERS) }
            )
        }

        composable(Routes.STICKERS) {
            StickersScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PARENT) {
            ParentZoneScreen(onBack = { navController.popBackStack() })
        }
    }
}
