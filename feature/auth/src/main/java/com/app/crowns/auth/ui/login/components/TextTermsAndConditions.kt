package com.app.crowns.auth.ui.login.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import com.app.crowns.designsystem.color.Primary

@Composable
fun TextTermsAndConditions(
    onClickTerms: () -> Unit = {},
    onClickPrivacy: () -> Unit = {}
) {
    val text = buildAnnotatedString {
        append("Al continuar, aceptas nuestros ")
        withLink(
            LinkAnnotation.Clickable(
                tag = "terminos",
                linkInteractionListener = { onClickTerms() },
                styles = TextLinkStyles(
                    style = SpanStyle(color = Primary)
                )
            )
        ) {
            append("Términos y Condiciones")
        }
        append(" y ")
        withLink(
            LinkAnnotation.Clickable(
                tag = "privacidad",
                linkInteractionListener = { onClickPrivacy() },
                styles = TextLinkStyles(
                    style = SpanStyle(color = Primary)
                )
            )
        ) {
            append("Política de Privacidad")
        }
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}