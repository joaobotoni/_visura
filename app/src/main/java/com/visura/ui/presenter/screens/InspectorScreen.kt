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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import com.visura.domain.repositories.inspector.Inspector
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
import com.visura.ui.viewmodels.InspectorEvent
import com.visura.ui.viewmodels.InspectorState
import com.visura.ui.viewmodels.InspectorViewModel

private data class InspectorEvents(
    val onNameChange: (String) -> Unit,
    val onCpfChange: (String) -> Unit,
    val onSave: () -> Unit
)

@Composable
fun InspectorScreen(
    viewModel: InspectorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarType by remember { mutableStateOf(SnackbarType.DEFAULT) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            snackbarType = when (event) {
                is InspectorEvent.SavedSuccessfully -> SnackbarType.SUCCESS
                is InspectorEvent.DeletedSuccessfully -> SnackbarType.SUCCESS
                is InspectorEvent.Error -> SnackbarType.ERROR
            }
            val message = when (event) {
                is InspectorEvent.SavedSuccessfully -> "Vistoriador salvo com sucesso!"
                is InspectorEvent.DeletedSuccessfully -> "Vistoriador removido com sucesso!"
                is InspectorEvent.Error -> event.exception.message ?: "Erro desconhecido"
            }
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    InspectorScreenContent(
        state = state,
        snackbar = SnackbarConfig(hostState = snackbarHostState, type = snackbarType),
        events = InspectorEvents(
            onNameChange = viewModel::setName,
            onCpfChange = viewModel::setCpf,
            onSave = viewModel::save
        )
    )
}

@Composable
private fun InspectorScreenContent(
    state: InspectorState,
    snackbar: SnackbarConfig,
    events: InspectorEvents
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
            item { InspectorHeader() }
            item { InspectorForm(state = state, events = events) }
            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = Alpha.Moderate)
                )
            }
            item { InspectorListHeader(count = state.inspectors.size) }
            if (state.inspectors.isEmpty()) {
                item { InspectorEmptyState() }
            } else {
                items(state.inspectors) { inspector ->
                    InspectorCard(inspector = inspector)
                }
            }
        }
    }
}

@Composable
private fun InspectorHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(Spacing.Huge))
        Text(
            text = "Vistoriadores",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.Small))
        Text(
            text = "Cadastre e gerencie os vistoriadores",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InspectorForm(
    state: InspectorState,
    events: InspectorEvents
) {
    StandardCard {
        Column(
            modifier = Modifier.padding(Spacing.XXLarge),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            StandardTextField(
                value = state.name,
                label = "Nome completo",
                placeholder = "Informe o nome do vistoriador",
                onValueChange = events.onNameChange,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            StandardTextField(
                value = state.cpf,
                label = "CPF",
                placeholder = "Informe o CPF",
                onValueChange = events.onCpfChange,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            StandardButton(
                text = if (state.isLoading) "Salvando..." else "Salvar Vistoriador",
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
private fun InspectorListHeader(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Vistoriadores Cadastrados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (count > 0) {
            androidx.compose.material3.Surface(
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
private fun InspectorEmptyState() {
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
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(IconSize.XXXLarge),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Nenhum vistoriador cadastrado",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Preencha o formulário acima para cadastrar um vistoriador",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InspectorCard(inspector: Inspector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.XLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.XXLarge),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize.XLarge)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = Alpha.Low)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(IconSize.Default)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.XXSmall)
            ) {
                Text(
                    text = inspector.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.XXSmall)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Badge,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(IconSize.XSmall)
                    )
                    Text(
                        text = inspector.cpf,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}