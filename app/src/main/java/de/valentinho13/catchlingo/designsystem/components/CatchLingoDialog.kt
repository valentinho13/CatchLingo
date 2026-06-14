package de.valentinho13.catchlingo.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.valentinho13.catchlingo.designsystem.CatchLingoColor

@Composable
fun CatchLingoDialog(
    title: String,
    body: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CatchLingoColor.WarmSurfaceRaised,
        titleContentColor = CatchLingoColor.TextPrimary,
        textContentColor = CatchLingoColor.TextMuted,
        shape = MaterialTheme.shapes.large,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(text = body, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            CatchLingoButton(text = confirmText, onClick = onConfirm)
        },
        dismissButton = {
            CatchLingoButton(text = dismissText, onClick = onDismiss, style = CatchLingoButtonStyle.Quiet)
        },
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 0.dp,
    )
}
