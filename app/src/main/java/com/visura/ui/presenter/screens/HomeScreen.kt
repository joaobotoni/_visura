package com.visura.ui.presenter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.visura.domain.vo.property.Property
import com.visura.ui.presenter.elements.snackbar.SnackbarType
import com.visura.ui.presenter.elements.snackbar.StandardSnackbar
import com.visura.ui.presenter.theme.Alpha
import com.visura.ui.presenter.theme.ComponentSize
import com.visura.ui.presenter.theme.IconSize
import com.visura.ui.presenter.theme.Spacing
import kotlinx.coroutines.launch

private data class HomeEvents(
    val onPropertyClick: (Property) -> Unit
)

private fun Property.toSelectionMessage() = "${type.displayName} em ${address.city}"

@Composable
fun Home() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val properties = remember { emptyList<Property>() }

    HomeContent(
        properties = properties,
        listState = listState,
        snackbarHostState = snackbarHostState,
        events = HomeEvents(
            onPropertyClick = { property ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = property.toSelectionMessage(),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    )
}

@Composable
private fun HomeContent(
    properties: List<Property>,
    listState: LazyListState,
    snackbarHostState: SnackbarHostState,
    events: HomeEvents
) {
    Scaffold(
        snackbarHost = {
            StandardSnackbar(hostState = snackbarHostState, type = SnackbarType.DEFAULT)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (properties.isEmpty()) {
                EmptyState()
            } else {
                HomePropertyList(
                    listState = listState,
                    properties = properties,
                    onClick = events.onPropertyClick
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.XXHuge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyStateIcon()
        Spacer(modifier = Modifier.height(Spacing.Huge))
        Text(
            text = "Nenhuma vistoria registrada",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.Medium))
        Text(
            text = "Suas inspeções aparecerão aqui. Acesse a aba de nova vistoria para começar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyStateIcon() {
    Box(
        modifier = Modifier
            .size(ComponentSize.XXLarge)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = Alpha.Moderate)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Assignment,
            contentDescription = null,
            modifier = Modifier.size(IconSize.XXXLarge),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun HomePropertyList(
    listState: LazyListState,
    properties: List<Property>,
    onClick: (Property) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = Spacing.Small, bottom = Spacing.XXLarge),
        verticalArrangement = Arrangement.spacedBy(Spacing.Huge)
    ) {
        items(
            items = properties,
            key = { it.id.toString() }
        ) { property ->
            PropertyCard(
                property = property,
                onClick = onClick
            )
        }
    }
}