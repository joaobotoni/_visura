package com.visura.ui.presenter.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.visura.domain.vo.location.Address
import com.visura.ui.viewmodels.RegisterUiState
import com.visura.ui.viewmodels.RegisterViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresPermission(
    allOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ]
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Register(viewModel: RegisterViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showConfirmInfoDialog by remember { mutableStateOf(false) }

    RequirePermissionLocation(
        onPermissionsGranted = { viewModel.fetchCurrentAddress() },
        onPermissionsDenied = { viewModel.clearError() }
    )

    Scaffold(
        floatingActionButton = {
            AnimatedLocationButton(
                onClick = { showBottomSheet = true },
                hasAddress = uiState.selectedAddress != null
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.selectedAddress != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                FinishButton(
                    onClick = { showConfirmInfoDialog = true },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HeaderSection(
                        currentStep = if (uiState.selectedAddress != null) 2 else 1,
                        totalSteps = 3
                    )
                }

                item {
                    ResidenceTypeSelector(
                        selectedType = uiState.selectedResidenceType,
                        onTypeSelected = { viewModel.updateResidenceType(it) }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = uiState.selectedAddress != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        uiState.selectedAddress?.let { address ->
                            SelectedAddressCard(
                                address = address,
                                onRemove = { viewModel.updateSelectedAddress(null) }
                            )
                        }
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = uiState.selectedAddress == null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        EmptyStateLocationCard()
                    }
                }
                item {
                    ProcessStepsCard()
                }
            }

            if (showBottomSheet) {
                LocationBottomSheet(
                    uiState = uiState,
                    onSearch = { query -> viewModel.searchAddress(query) },
                    onCurrentLocationClick = { viewModel.fetchCurrentAddress() },
                    onAddressClick = { address ->
                        viewModel.updateSelectedAddress(address)
                        showBottomSheet = false
                    },
                    onDismiss = {
                        showBottomSheet = false
                        viewModel.clearError()
                    }
                )
            }

            if (showConfirmInfoDialog) {
                InfoConfirmDialog(
                    residenceType = uiState.selectedResidenceType,
                    address = uiState.selectedAddress,
                    onConfirm = {
                        showConfirmInfoDialog = false
                    },
                    onDismiss = {
                        showConfirmInfoDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun FinishButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Finalizar Cadastro",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HeaderSection(currentStep: Int, totalSteps: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cadastre o Imóvel",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Preencha as informações básicas para começar",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        ProgressIndicator(currentStep = currentStep, totalSteps = totalSteps)
    }
}

@Composable
fun ProgressIndicator(currentStep: Int, totalSteps: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Etapa $currentStep de $totalSteps",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "${(currentStep * 100) / totalSteps}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / totalSteps },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
        }
    }
}

@Composable
fun EmptyStateLocationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PulsingLocationIcon()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Adicione a Localização",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toque no botão abaixo para buscar ou selecionar sua localização atual",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PulsingLocationIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .alpha(alpha)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun SelectedAddressCard(address: Address, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Endereço Selecionado",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAddressPrimaryText(address),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildAddressSecondaryText(address),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remover endereço",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun ProcessStepsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Como Funciona",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProcessStep(
                icon = Icons.Outlined.Home,
                title = "Defina o Tipo",
                description = "Escolha se é residencial, comercial ou não residencial",
                stepNumber = 1
            )

            ProcessStep(
                icon = Icons.Outlined.LocationOn,
                title = "Adicione a Localização",
                description = "Busque ou use sua localização atual",
                stepNumber = 2
            )

            ProcessStep(
                icon = Icons.Outlined.Check,
                title = "Finalize o Cadastro",
                description = "Complete as informações e salve",
                stepNumber = 3,
                isLast = true
            )
        }
    }
}

@Composable
fun ProcessStep(
    icon: ImageVector,
    title: String,
    description: String,
    stepNumber: Int,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(48.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (!isLast) 16.dp else 0.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AnimatedLocationButton(onClick: () -> Unit, hasAddress: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (hasAddress) 1.0f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fab_scale"
    )

    FloatingActionButton(
        onClick = onClick,
        containerColor = if (hasAddress)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.primaryContainer,
        contentColor = if (hasAddress)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (hasAddress) Icons.Filled.Edit else Icons.Filled.LocationOn,
                contentDescription = if (hasAddress) "Editar localização" else "Selecionar localização",
                modifier = Modifier
                    .size(28.dp)
                    .padding(2.dp),
                tint = if (hasAddress)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ResidenceTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Tipo de Residência",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val types = listOf("Comercial", "Não Residencial", "Residencial")
            types.forEach { type ->
                ResidenceTypeOption(
                    type = type,
                    isSelected = selectedType == type,
                    onClick = { onTypeSelected(type) }
                )
            }
        }
    }
}

@Composable
fun ResidenceTypeOption(
    type: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "bg_color"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = type,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationBottomSheet(
    uiState: RegisterUiState,
    onSearch: (String) -> Unit,
    onCurrentLocationClick: () -> Unit,
    onAddressClick: (Address) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var query by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            LocationSearchBar(
                query = query,
                onQueryChange = { newQuery ->
                    query = newQuery
                    onSearch(newQuery)
                },
                onCurrentLocationClick = onCurrentLocationClick
            )

            if (query.isNotEmpty() || uiState.isLoading || uiState.addresses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                SearchResults(
                    uiState = uiState,
                    onAddressClick = onAddressClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCurrentLocationClick: () -> Unit
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = { },
                expanded = false,
                onExpandedChange = { },
                placeholder = {
                    Text(
                        text = "Digite sua localização",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Localização atual",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = onCurrentLocationClick)
                    )
                }
            )
        },
        expanded = false,
        onExpandedChange = { },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {}
}

@Composable
fun SearchResults(
    uiState: RegisterUiState,
    onAddressClick: (Address) -> Unit
) {
    when {
        uiState.isLoading -> LoadingState()
        uiState.error != null -> ErrorState(error = uiState.error)
        uiState.addresses.isEmpty() -> EmptyState()
        else -> AddressList(addresses = uiState.addresses, onAddressClick = onAddressClick)
    }
}

@Composable
fun AddressList(
    addresses: Set<Address>,
    onAddressClick: (Address) -> Unit
) {
    val addressList = addresses.toList()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(items = addressList, key = { it.hashCode() }) { address ->
            AddressItem(address = address, onClick = { onAddressClick(address) })
            if (address != addressList.last()) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun AddressItem(address: Address, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAddressPrimaryText(address),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buildAddressSecondaryText(address),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Buscando endereços...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorState(error: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum endereço encontrado",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InfoConfirmDialog(
    residenceType: String,
    address: Address?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null) },
        title = {
            Text(text = "Confirmar Cadastro")
        },
        text = {
            Column {
                Text("Você está prestes a cadastrar o imóvel:")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tipo: $residenceType",
                    fontWeight = FontWeight.Bold
                )
                address?.let {
                    Text(
                        text = "Local: ${buildAddressPrimaryText(it)}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequirePermissionLocation(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
) {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            onPermissionsGranted()
        } else {
            onPermissionsDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
}

fun buildAddressPrimaryText(address: Address): String {
    return "${address.street}, ${address.number}"
}

fun buildAddressSecondaryText(address: Address): String {
    return listOfNotNull(address.neighborhood, address.city, address.state)
        .filter { it.isNotBlank() }
        .joinToString(" - ")
}