package com.app.crowns.vently.navigation.transition

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

object DefaultTransitions {
    val enter: EnterTransition =
        slideInHorizontally(initialOffsetX = { it }) + fadeIn()

    val exit: ExitTransition =
        slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()

    val popEnter: EnterTransition =
        slideInHorizontally(initialOffsetX = { -it }) + fadeIn()

    val popExit: ExitTransition =
        slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
}