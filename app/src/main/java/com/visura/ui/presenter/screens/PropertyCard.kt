package com.visura.ui.presenter.screens.property

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.visura.domain.vo.location.Address
import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyCategory
import com.visura.domain.vo.property.PropertyType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun PropertyCard(
    property: Property,
    onClick: (Property) -> Unit,
    onEditClick: (Property) -> Unit,
    onDeleteClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(onConfirm = {
            showDeleteDialog = false
            onDeleteClick(property)
        }, onDismiss = { showDeleteDialog = false })
    }

    Card(
        onClick = { onClick(property) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            PropertyCardBanner(
                type = property.type,
                category = property.category,
                menuExpanded = menuExpanded,
                onMenuToggle = { menuExpanded = !menuExpanded },
                onMenuDismiss = { menuExpanded = false },
                onEdit = {
                    menuExpanded = false
                    onEditClick(property)
                },
                onDelete = {
                    menuExpanded = false
                    showDeleteDialog = true
                })
            PropertyCardBody(
                address = property.address, created = property.created
            )
        }
    }
}

@Composable
private fun PropertyCardBanner(
    type: PropertyType,
    category: PropertyCategory,
    menuExpanded: Boolean,
    onMenuToggle: () -> Unit,
    onMenuDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val bannerColor = MaterialTheme.colorScheme.primaryContainer
    val onBannerColor = MaterialTheme.colorScheme.onPrimaryContainer

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(bannerColor, bannerColor.copy(alpha = 0.55f))
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = resolvePropertyIcon(category),
                contentDescription = null,
                tint = onBannerColor,
                modifier = Modifier.size(28.dp)
            )

            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = type.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onBannerColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = onBannerColor.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            PropertyContextMenu(
                expanded = menuExpanded,
                onToggle = onMenuToggle,
                onDismiss = onMenuDismiss,
                onEdit = onEdit,
                onDelete = onDelete,
                iconTint = onBannerColor
            )
        }
    }
}

@Composable
private fun PropertyCardBody(
    address: Address, created: Instant
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AddressInfoRow(
            icon = Icons.Outlined.LocationOn,
            text = formatPrimaryAddress(address),
            emphasized = true
        )

        val secondary = formatSecondaryAddress(address)
        if (secondary.isNotBlank()) {
            AddressInfoRow(icon = Icons.Outlined.Map, text = secondary)
        }

        if (address.postalCode.isNotBlank()) {
            AddressInfoRow(
                icon = Icons.Outlined.MarkunreadMailbox, text = "CEP ${address.postalCode}"
            )
        }

        if (address.complement.isNotBlank()) {
            AddressInfoRow(icon = Icons.Outlined.Apartment, text = address.complement)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 2.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )

        AddressInfoRow(
            icon = Icons.Outlined.CalendarToday,
            text = "Registrado em ${formatDate(created)}",
            muted = true
        )
    }
}

@Composable
private fun AddressInfoRow(
    icon: ImageVector, text: String, emphasized: Boolean = false, muted: Boolean = false
) {
    val textColor = when {
        emphasized -> MaterialTheme.colorScheme.onSurface
        muted -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val iconColor = when {
        emphasized -> MaterialTheme.colorScheme.primary
        muted -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = text,
            style = if (emphasized) MaterialTheme.typography.bodyMedium
            else MaterialTheme.typography.bodySmall,
            fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun PropertyContextMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    iconTint: Color
) {
    Box {
        IconButton(
            onClick = onToggle, modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "Mais opções",
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded, onDismissRequest = onDismiss
        ) {
            DropdownMenuItem(
                text = {
                Text(
                    text = "Editar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = onEdit,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            )
            DropdownMenuItem(
                text = {
                Text(
                    text = "Excluir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = onDelete,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Excluir propriedade?") },
        text = { Text("Esta ação não pode ser desfeita.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm, colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Excluir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        })
}

// endregion

// region FORMATTERS

private fun resolvePropertyIcon(category: PropertyCategory): ImageVector = when (category) {
    PropertyCategory.HOME -> Icons.Outlined.Home
    PropertyCategory.APARTMENT -> Icons.Outlined.Apartment
}

private fun formatPrimaryAddress(address: Address): String = when {
    address.street.isNotBlank() && address.neighborhood.isNotBlank() -> "${address.street}, ${address.neighborhood}"
    address.street.isNotBlank() -> address.street
    address.neighborhood.isNotBlank() -> address.neighborhood
    address.city.isNotBlank() -> address.city
    else -> "Endereço não informado"
}

private fun formatSecondaryAddress(address: Address): String = buildList {
    if (address.number.isNotBlank()) add("Nº ${address.number}")
    if (address.city.isNotBlank()) add(address.city)
    if (address.state.isNotBlank()) add(address.state)
}.joinToString(" · ")

private fun formatDate(instant: Instant): String =
    DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault()).format(instant)
