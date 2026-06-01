package com.visura.ui.presenter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.visura.domain.repositories.inspection.Inspection
import com.visura.domain.vo.inspection.InspectionStatus
import com.visura.domain.vo.inspection.InspectionType
import com.visura.ui.presenter.elements.badge.StandardSelectionBadge
import com.visura.ui.presenter.elements.button.StandardButton
import com.visura.ui.presenter.elements.card.StandardCard
import com.visura.ui.presenter.elements.field.StandardTextField
import com.visura.ui.presenter.elements.snackbar.SnackbarConfig
import com.visura.ui.presenter.elements.snackbar.SnackbarType
import com.visura.ui.presenter.elements.snackbar.StandardSnackbar
import com.visura.ui.presenter.theme.Alpha
import com.visura.ui.presenter.theme.ComponentSize
import com.visura.ui.presenter.theme.CornerRadius
import com.visura.ui.presenter.theme.IconSize
import com.visura.ui.presenter.theme.Spacing
import com.visura.ui.viewmodels.InspectionEvent
import com.visura.ui.viewmodels.InspectionState
import com.visura.ui.viewmodels.InspectionViewModel

private data class InspectionEvents(
    val onPropertyIdChange: (String) -> Unit,
    val onInspectorIdChange: (String) -> Unit,
    val onTypeSelect: (InspectionType) -> Unit,
    val onStatusSelect: (InspectionStatus) -> Unit,
    val onObservationChange: (String) -> Unit,
    val onSave: () -> Unit
)

@Composable
fun InspectionScreen(
    viewModel: InspectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarType by remember { mutableStateOf(SnackbarType.DEFAULT) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            snackbarType = when (event) {
                is InspectionEvent.SavedSuccessfully -> SnackbarType.SUCCESS
                is InspectionEvent.DeletedSuccessfully -> SnackbarType.SUCCESS
                is InspectionEvent.Error -> SnackbarType.ERROR
            }
            val message = when (event) {
                is InspectionEvent.SavedSuccessfully -> "Vistoria salva com sucesso!"
                is InspectionEvent.DeletedSuccessfully -> "Vistoria removida com sucesso!"
                is InspectionEvent.Error -> event.exception.message ?: "Erro desconhecido"
            }
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    InspectionScreenContent(
        state = state,
        snackbar = SnackbarConfig(hostState = snackbarHostState, type = snackbarType),
        events = InspectionEvents(
            onPropertyIdChange = viewModel::setPropertyId,
            onInspectorIdChange = viewModel::setInspectorId,
            onTypeSelect = viewModel::setType,
            onStatusSelect = viewModel::setStatus,
            onObservationChange = viewModel::setObservation,
            onSave = viewModel::save
        )
    )
}

@Composable
private fun InspectionScreenContent(
    state: InspectionState,
    snackbar: SnackbarConfig,
    events: InspectionEvents
) {
    Scaffold(
        snackbarHost = {
            StandardSnackbar(hostState = snackbar.hostState, type = snackbar.type)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(Spacing.Large),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            item { InspectionHeader() }
            item { InspectionForm(state = state, events = events) }
            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = Alpha.Moderate)
                )
            }
            item {
                InspectionListHeader(count = state.inspections.size)
            }
            if (state.inspections.isEmpty()) {
                item { InspectionEmptyState() }
            } else {
                items(state.inspections) { inspection ->
                    InspectionCard(inspection = inspection)
                }
            }
        }
    }
}

@Composable
private fun InspectionHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(Spacing.Huge))
        Text(
            text = "Nova Vistoria",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.Small))
        Text(
            text = "Preencha os dados para registrar uma vistoria",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InspectionForm(
    state: InspectionState,
    events: InspectionEvents
) {
    StandardCard {
        Column(
            modifier = Modifier.padding(Spacing.XXLarge),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            StandardTextField(
                value = state.propertyId,
                label = "ID do Imóvel",
                placeholder = "Informe o ID do imóvel",
                onValueChange = events.onPropertyIdChange,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            StandardTextField(
                value = state.inspectorId,
                label = "ID do Vistoriador",
                placeholder = "Informe o ID do vistoriador",
                onValueChange = events.onInspectorIdChange,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            InspectionTypeSelector(
                selected = state.selectedType,
                onSelect = events.onTypeSelect
            )

            InspectionStatusSelector(
                selected = state.selectedStatus,
                onSelect = events.onStatusSelect
            )

            StandardTextField(
                value = state.generalObservation,
                label = "Observações gerais",
                placeholder = "Descreva observações sobre a vistoria",
                onValueChange = events.onObservationChange,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            StandardButton(
                text = if (state.isLoading) "Salvando..." else "Salvar Vistoria",
                onClick = events.onSave,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ComponentSize.Medium)
            )
        }
    }
}

@Composable
private fun InspectionTypeSelector(
    selected: InspectionType?,
    onSelect: (InspectionType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            Icon(
                imageVector = Icons.Outlined.Assignment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconSize.Default)
            )
            Text(
                text = "Tipo de Vistoria",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            contentPadding = PaddingValues(horizontal = Spacing.XXSmall)
        ) {
            items(InspectionType.entries) { type ->
                StandardSelectionBadge(
                    label = type.description,
                    isSelected = selected == type,
                    onClick = { onSelect(type) }
                )
            }
        }
    }
}

@Composable
private fun InspectionStatusSelector(
    selected: InspectionStatus,
    onSelect: (InspectionStatus) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(IconSize.Default)
            )
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            contentPadding = PaddingValues(horizontal = Spacing.XXSmall)
        ) {
            items(InspectionStatus.entries) { status ->
                StandardSelectionBadge(
                    label = status.description,
                    isSelected = selected == status,
                    onClick = { onSelect(status) }
                )
            }
        }
    }
}

@Composable
private fun InspectionListHeader(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Vistorias Registradas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (count > 0) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(
                        horizontal = Spacing.Medium,
                        vertical = Spacing.XXSmall
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun InspectionEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.XXHuge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Large)
    ) {
        Box(
            modifier = Modifier
                .size(ComponentSize.XLarge)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = Alpha.Low)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Assignment,
                contentDescription = null,
                modifier = Modifier.size(IconSize.XXXLarge),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Nenhuma vistoria registrada",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Preencha o formulário acima para cadastrar sua primeira vistoria",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InspectionCard(inspection: Inspection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.XLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.XXLarge)) {
            InspectionCardHeader(inspection = inspection)
            Spacer(modifier = Modifier.height(Spacing.Medium))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = Alpha.Moderate)
            )
            Spacer(modifier = Modifier.height(Spacing.Medium))
            InspectionCardBody(inspection = inspection)
        }
    }
}

@Composable
private fun InspectionCardHeader(inspection: Inspection) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = inspection.inspectionType.description,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
            shape = RoundedCornerShape(percent = 50),
            color = when (inspection.inspectionStatus) {
                InspectionStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                InspectionStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
                InspectionStatus.CANCELED -> MaterialTheme.colorScheme.error
                InspectionStatus.SCHEDULED -> MaterialTheme.colorScheme.secondary
            }.copy(alpha = Alpha.XLow)
        ) {
            Text(
                text = inspection.inspectionStatus.description,
                modifier = Modifier.padding(
                    horizontal = Spacing.Medium,
                    vertical = Spacing.XXSmall
                ),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = when (inspection.inspectionStatus) {
                    InspectionStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                    InspectionStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
                    InspectionStatus.CANCELED -> MaterialTheme.colorScheme.error
                    InspectionStatus.SCHEDULED -> MaterialTheme.colorScheme.secondary
                }
            )
        }
    }
}

@Composable
private fun InspectionCardBody(inspection: Inspection) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        InspectionInfoRow(
            icon = Icons.Outlined.Home,
            label = "Imóvel",
            value = inspection.propertyId
        )
        InspectionInfoRow(
            icon = Icons.Outlined.Person,
            label = "Vistoriador",
            value = inspection.inspectorId
        )
        InspectionInfoRow(
            icon = Icons.Outlined.CalendarToday,
            label = "Data",
            value = inspection.scheduledDate.toString()
        )
        if (inspection.generalObservation.isNotBlank()) {
            InspectionInfoRow(
                icon = Icons.Outlined.Assignment,
                label = "Observação",
                value = inspection.generalObservation
            )
        }
    }
}

@Composable
private fun InspectionInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(IconSize.XSmall)
        )
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}