package com.visura.ui.presenter.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalPostOffice
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.visura.domain.vo.location.Address
import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyCategory
import com.visura.domain.vo.property.PropertyType
import com.visura.ui.presenter.theme.Alpha
import com.visura.ui.presenter.theme.CornerRadius
import com.visura.ui.presenter.theme.Elevation
import com.visura.ui.presenter.theme.IconSize
import com.visura.ui.presenter.theme.Spacing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault())

private data class PropertyCardEvents(
    val onPropertyClick: (Property) -> Unit
)

private data class AddressRowStyle(
    val iconSize: Dp,
    val iconColor: Color,
    val textColor: Color,
    val textStyle: TextStyle,
    val textWeight: FontWeight
)

@Composable
fun PropertyCard(
    property: Property,
    onClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    PropertyCardContent(
        property = property,
        modifier = modifier,
        events = PropertyCardEvents(onPropertyClick = onClick)
    )
}

@Composable
private fun PropertyCardContent(
    property: Property,
    modifier: Modifier,
    events: PropertyCardEvents
) {
    Card(
        onClick = { events.onPropertyClick(property) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Large, vertical = Spacing.Small),
        shape = RoundedCornerShape(CornerRadius.XXLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.None),
        border = BorderStroke(Spacing.Hairline, MaterialTheme.colorScheme.outlineVariant.copy(alpha = Alpha.XXLow))
    ) {
        Column(modifier = Modifier.padding(Spacing.XXLarge)) {
            PropertyCardHeader(
                type = property.type,
                category = property.category,
                created = property.created
            )
            Spacer(modifier = Modifier.height(Spacing.XLarge))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = Alpha.Moderate))
            Spacer(modifier = Modifier.height(Spacing.Large))
            PropertyCardBody(address = property.address)
        }
    }
}

@Composable
private fun PropertyCardHeader(
    type: PropertyType,
    category: PropertyCategory,
    created: Instant
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIcon(category = category)
            Spacer(modifier = Modifier.width(Spacing.Medium))
            PropertyTypeLabels(type = type, category = category)
        }
        Spacer(modifier = Modifier.width(Spacing.Large))
        DateBadge(date = created.toFormattedString())
    }
}

@Composable
private fun PropertyTypeLabels(type: PropertyType, category: PropertyCategory) {
    Column {
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategoryIcon(category: PropertyCategory) {
    val icon = when (category) {
        PropertyCategory.HOME -> Icons.Outlined.Home
        PropertyCategory.APARTMENT -> Icons.Outlined.Apartment
        else -> Icons.Outlined.Home
    }

    Box(
        modifier = Modifier
            .size(IconSize.XLarge)
            .clip(RoundedCornerShape(CornerRadius.Large))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = Alpha.Low))
            .border(
                Spacing.Hairline,
                MaterialTheme.colorScheme.primary.copy(alpha = Alpha.XLow),
                RoundedCornerShape(CornerRadius.Large)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(IconSize.Default)
        )
    }
}

@Composable
private fun DateBadge(date: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape
    ) {
        Text(
            text = date,
            modifier = Modifier.padding(horizontal = Spacing.SemiSmall, vertical = Spacing.XSmall),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun PropertyCardBody(address: Address) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.SemiSmall)) {
        AddressInfoRow(
            icon = Icons.Outlined.LocationOn,
            text = address.toPrimaryString(),
            isPrimary = true
        )
        address.toCityStateString()?.let {
            AddressInfoRow(icon = Icons.Outlined.Map, text = it)
        }
        if (address.postalCode.isNotBlank()) {
            AddressInfoRow(
                icon = Icons.Outlined.LocalPostOffice,
                text = "CEP ${address.postalCode}"
            )
        }
        if (address.complement.isNotBlank()) {
            AddressInfoRow(
                icon = Icons.Outlined.Info,
                text = address.complement
            )
        }
    }
}

@Composable
private fun addressRowStyle(isPrimary: Boolean): AddressRowStyle = if (isPrimary) {
    AddressRowStyle(
        iconSize = IconSize.Small,
        iconColor = MaterialTheme.colorScheme.primary,
        textColor = MaterialTheme.colorScheme.onSurface,
        textStyle = MaterialTheme.typography.bodyMedium,
        textWeight = FontWeight.SemiBold
    )
} else {
    AddressRowStyle(
        iconSize = IconSize.XSmall,
        iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
        textStyle = MaterialTheme.typography.bodySmall,
        textWeight = FontWeight.Normal
    )
}

@Composable
private fun AddressInfoRow(
    icon: ImageVector,
    text: String,
    isPrimary: Boolean = false
) {
    val style = addressRowStyle(isPrimary)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.SemiSmall),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = style.iconColor,
            modifier = Modifier.size(style.iconSize)
        )
        Text(
            text = text,
            style = style.textStyle,
            fontWeight = style.textWeight,
            color = style.textColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun Instant.toFormattedString(): String = dateFormatter.format(this)