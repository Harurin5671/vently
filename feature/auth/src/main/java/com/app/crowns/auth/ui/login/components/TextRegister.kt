package com.app.crowns.auth.ui.login.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import com.app.crowns.designsystem.color.Primary

@Composable
fun TextRegister(onClickRegister: () -> Unit) {

    val text = buildAnnotatedString {
        append("No tienes cuenta? ")

        withLink(
            LinkAnnotation.Clickable(
                tag = "registro",
                linkInteractionListener = {
                    onClickRegister()
                },
                styles = TextLinkStyles(
                    style = SpanStyle(color = Primary)
                )
            )
        ) {
            append("Regístrate")
        }
    }

    Text(text = text)
}