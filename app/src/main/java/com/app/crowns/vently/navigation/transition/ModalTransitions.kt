package com.app.crowns.vently.navigation.transition

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

object ModalTransitions {
    val enter: EnterTransition = fadeIn()
    val exit: ExitTransition = fadeOut()
}