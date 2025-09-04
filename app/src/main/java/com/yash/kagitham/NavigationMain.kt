package com.yash.kagitham

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yash.sdk.AppRegistry

@Composable
fun NavigationMain () {
    val navController = rememberNavController()


    LaunchedEffect(Unit) {
        AppRegistry.setNavController(navController)
    }

    NavHost(
        navController = navController, startDestination = "App",
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(400)
            )
        },
        exitTransition = {
            slideOutOfContainer (
                AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(400)
            )

        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(400)
            )
        },
        popExitTransition = {
            slideOutOfContainer (
                AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(400)
            )
        },
    ) {
        composable(
            "App"
        ){
            App()
        }
    }

}

