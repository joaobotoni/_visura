package com.visura.ui.presenter.elements.snackbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.visura.ui.presenter.theme.CornerRadius
import com.visura.ui.presenter.theme.Elevation
import com.visura.ui.presenter.theme.Spacing

enum class SnackbarType(
    val containerColor: Color,
    val contentColor: Color,
    val iconColor: Color,
    val icon: ImageVector
) {
    SUCCESS(
        containerColor = Color(0xFF104C12),
        contentColor = Color.White,
        iconColor = Color(0xFF4CAF50),
        icon = Icons.Default.CheckCircle
    ),
    ERROR(
        containerColor = Color(0xFFDD493E),
        contentColor = Color.White,
        iconColor = Color.White,
        icon = Icons.Outlined.Warning
    ),
    DEFAULT(
        containerColor = Color(0xFF424242),
        contentColor = Color.White,
        iconColor = Color.White,
        icon = Icons.Outlined.Warning
    )
}

data class SnackbarConfig(
    val hostState: SnackbarHostState,
    val type: SnackbarType
)

@Composable
fun StandardSnackbar(
    hostState: SnackbarHostState,
    type: SnackbarType = SnackbarType.DEFAULT
) {
    SnackbarHost(
        hostState = hostState,
        modifier = Modifier.padding(Spacing.Large)
    ) { data ->
        Card(
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = type.containerColor,
                contentColor = type.contentColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.Default)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Large, vertical = Spacing.Small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MessageContent(
                    message = data.visuals.message,
                    type = type,
                    modifier = Modifier.weight(1f)
                )
                DismissButton(
                    onDismiss = data::dismiss,
                    tint = type.contentColor
                )
            }
        }
    }
}

@Composable
private fun MessageContent(
    message: String,
    type: SnackbarType,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)
    ) {
        Icon(
            imageVector = type.icon,
            contentDescription = null,
            tint = type.iconColor
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DismissButton(onDismiss: () -> Unit, tint: Color) {
    IconButton(
        onClick = onDismiss,
        modifier = Modifier.padding(start = Spacing.Small)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = tint
        )
    }
}
