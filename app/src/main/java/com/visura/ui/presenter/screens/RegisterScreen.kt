package com.visura.ui.presenter.screens

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.visura.R
import com.visura.domain.vo.location.Address
import com.visura.domain.vo.property.PropertyCategory
import com.visura.domain.vo.property.PropertyType
import com.visura.ui.presenter.elements.badge.StandardSelectionBadge
import com.visura.ui.presenter.elements.button.StandardTextButton
import com.visura.ui.presenter.elements.card.StandardCard
import com.visura.ui.presenter.theme.Alpha
import com.visura.ui.presenter.theme.ComponentSize
import com.visura.ui.presenter.theme.CornerRadius
import com.visura.ui.presenter.theme.Elevation
import com.visura.ui.presenter.theme.IconSize
import com.visura.ui.presenter.theme.PulseAnimation
import com.visura.ui.presenter.theme.Spacing
import com.visura.ui.viewmodels.RegisterState
import com.visura.ui.viewmodels.RegisterViewModel
import kotlinx.coroutines.launch

private const val TOTAL_STEPS = 3
private const val STEP_WITH_ADDRESS = 2
private const val STEP_INITIAL = 1
private const val LOCATION_ITEM_INDEX = 3

data class RegisterEvents(
    val onPropertySelected: (PropertyType) -> Unit,
    val onCategorySelected: (PropertyCategory) -> Unit,
    val onLocationRequest: () -> Unit,
    val onLocationSearch: (String) -> Unit,
    val onAddressSelected: (Address) -> Unit,
    val onAddressRemoved: () -> Unit,
    val onDismissLocationSheet: () -> Unit,
    val onSubmit: () -> Unit,
    val onLocationGranted: () -> Unit,
    val onCameraGranted: () -> Unit
)

data class RegisterCurrentState(
    val isReadyToSubmit: Boolean,
    val canAddLocation: Boolean,
    val currentStep: Int,
    val showLocationSheet: Boolean
)

data class SelectionData<T>(
    val title: String,
    val icon: ImageVector,
    val items: List<T>,
    val selectedItem: T?
)

data class SelectionCallbacks<T>(
    val onSelect: (T) -> Unit,
    val labelSelector: (T) -> String,
    val iconSelector: ((T) -> ImageVector)? = null
)

@RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Register(viewModel: RegisterViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showLocationSheet by rememberSaveable { mutableStateOf(false) }

    LocationPermissionHandler(onGranted = viewModel::fetchCurrentAddress)
    CameraPermissionHandler(onGranted = {})

    RegisterScaffold(
        state = state,
        currentState = formState(state, showLocationSheet),
        events = formEvents(
            viewModel = viewModel,
            setLocationSheetVisible = { showLocationSheet = it })
    )
}

private fun formState(state: RegisterState, showLocationSheet: Boolean) = RegisterCurrentState(
    isReadyToSubmit = state.selectedPropertyCategory != null && state.selectedPropertyType != null && state.selectedAddress != null,
    canAddLocation = state.selectedPropertyType != null && state.selectedPropertyCategory != null,
    currentStep = if (state.selectedAddress != null) STEP_WITH_ADDRESS else STEP_INITIAL,
    showLocationSheet = showLocationSheet
)

private fun formEvents(
    viewModel: RegisterViewModel,
    setLocationSheetVisible: (Boolean) -> Unit
) = RegisterEvents(
    onPropertySelected = viewModel::setPropertyType,
    onCategorySelected = viewModel::setPropertyCategory,
    onLocationRequest = { setLocationSheetVisible(true) },
    onLocationSearch = viewModel::setSearchQuery,
    onAddressSelected = {
        viewModel.setAddress(it)
        setLocationSheetVisible(false)
    },
    onAddressRemoved = { viewModel.setAddress(null) },
    onDismissLocationSheet = { setLocationSheetVisible(false) },
    onSubmit = viewModel::validateAndFinish,
    onLocationGranted = viewModel::fetchCurrentAddress,
    onCameraGranted = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScaffold(
    state: RegisterState,
    currentState: RegisterCurrentState,
    events: RegisterEvents
) {
    Scaffold(
        bottomBar = {
            SubmitButton(visible = currentState.isReadyToSubmit, onClick = events.onSubmit)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            RegisterForm(state = state, currentState = currentState, events = events)
            if (currentState.showLocationSheet) {
                LocationSearchSheet(state = state, events = events)
            }
        }
    }
}

@Composable
private fun RegisterForm(
    state: RegisterState,
    currentState: RegisterCurrentState,
    events: RegisterEvents
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.selectedAddress) {
        listState.animateScrollToItem(LOCATION_ITEM_INDEX)
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(Spacing.Large),
        verticalArrangement = Arrangement.spacedBy(Spacing.Large)
    ) {
        item { HeaderSection(currentStep = currentState.currentStep) }
        item {
            PropertySelectionGroup(
                data = SelectionData(
                    title = "Tipo do Imóvel",
                    icon = Icons.Outlined.Home,
                    items = PropertyType.entries,
                    selectedItem = state.selectedPropertyType
                ),
                callbacks = SelectionCallbacks(
                    onSelect = events.onPropertySelected,
                    labelSelector = { it.displayName }
                )
            )
        }
        item {
            PropertySelectionGroup(
                data = SelectionData(
                    title = "Categoria",
                    icon = Icons.Outlined.Category,
                    items = PropertyCategory.entries,
                    selectedItem = state.selectedPropertyCategory
                ),
                callbacks = SelectionCallbacks(
                    onSelect = events.onCategorySelected,
                    labelSelector = { it.displayName },
                    iconSelector = { categoryIcon(it) }
                )
            )
        }
        item {
            LocationSection(
                address = state.selectedAddress,
                canAddLocation = currentState.canAddLocation,
                events = events
            )
        }
    }
}

private fun categoryIcon(category: PropertyCategory): ImageVector = when (category) {
    PropertyCategory.HOME -> Icons.Outlined.Home
    PropertyCategory.APARTMENT -> Icons.Outlined.Apartment
}

@Composable
private fun HeaderSection(currentStep: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(Spacing.Huge))
        Text(
            text = "Cadastre o Imóvel",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.Small))
        Text(
            text = "Preencha as informações para começar",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.XXLarge))
        ProgressCard(currentStep = currentStep)
    }
}

@Composable
private fun ProgressCard(currentStep: Int) {
    val progress = currentStep / TOTAL_STEPS.toFloat()
    val percentage = currentStep * 100 / TOTAL_STEPS

    StandardCard {
        Column(modifier = Modifier.padding(Spacing.Large)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Etapa $currentStep de $TOTAL_STEPS",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PercentageBadge(percentage = percentage)
            }
            Spacer(modifier = Modifier.height(Spacing.Medium))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Spacing.Small)
                    .clip(MaterialTheme.shapes.small),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}

@Composable
private fun PercentageBadge(percentage: Int) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = "$percentage%",
            modifier = Modifier.padding(horizontal = Spacing.Medium, vertical = Spacing.XSmall),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun <T> PropertySelectionGroup(
    data: SelectionData<T>,
    callbacks: SelectionCallbacks<T>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(icon = data.icon, title = data.title)
        Spacer(modifier = Modifier.height(Spacing.Medium))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = Spacing.Large),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            items(data.items) { item ->
                StandardSelectionBadge(
                    label = callbacks.labelSelector(item),
                    isSelected = data.selectedItem == item,
                    onClick = { callbacks.onSelect(item) },
                    icon = callbacks.iconSelector?.invoke(item)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(IconSize.Default)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LocationSection(
    address: Address?,
    canAddLocation: Boolean,
    events: RegisterEvents
) {
    AnimatedContent(
        targetState = address,
        transitionSpec = {
            (fadeIn(tween(220, easing = FastOutSlowInEasing)) +
                    scaleIn(
                        tween(220, easing = FastOutSlowInEasing),
                        initialScale = 0.96f
                    )) togetherWith
                    (fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.96f))
        },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(tween(220, easing = FastOutSlowInEasing))
    ) { targetAddress ->
        if (targetAddress == null) {
            EmptyLocationCard(enabled = canAddLocation, onClick = events.onLocationRequest)
        } else {
            SelectedAddressCard(
                address = targetAddress,
                onEdit = events.onLocationRequest,
                onRemove = events.onAddressRemoved
            )
        }
    }
}

@Composable
private fun EmptyLocationCard(enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable(enabled = enabled, onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (enabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = Alpha.Medium)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = Alpha.XLow)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Huge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LocationIconContainer(isActive = enabled)
            Spacer(modifier = Modifier.height(Spacing.XXLarge))
            Text(
                text = if (enabled) "Adicione a Localização" else "Aguardando Seleção",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = Alpha.High),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Spacing.Medium))
            Text(
                text = if (enabled) "Toque aqui para buscar ou usar sua localização atual"
                else "Selecione o tipo e categoria do imóvel primeiro",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (enabled) Alpha.High else Alpha.Medium
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LocationIconContainer(isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(ComponentSize.XLarge)
            .clip(CircleShape)
            .background(
                if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = Alpha.Low)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = Alpha.XXLow)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            PulsingLocationIcon()
        } else {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = Alpha.Medium),
                modifier = Modifier.size(IconSize.XLarge)
            )
        }
    }
}

@Composable
private fun PulsingLocationIcon() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = PulseAnimation.InitialScale,
        targetValue = PulseAnimation.TargetScale,
        animationSpec = infiniteRepeatable(
            tween(PulseAnimation.DurationMs, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by transition.animateFloat(
        initialValue = PulseAnimation.InitialAlpha,
        targetValue = PulseAnimation.TargetAlpha,
        animationSpec = infiniteRepeatable(
            tween(PulseAnimation.DurationMs, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(ComponentSize.Large)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(IconSize.Large)
        )
    }
}

@Composable
private fun SelectedAddressCard(address: Address, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.XXLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.None),
        border = BorderStroke(
            Spacing.Hairline,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = Alpha.XXLow)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.XLarge),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectedAddressIcon()
            AddressCardContent(address = address, modifier = Modifier.weight(1f))
            AddressCardActions(onEdit = onEdit, onRemove = onRemove)
        }
    }
}

@Composable
private fun SelectedAddressIcon() {
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
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(IconSize.Default)
        )
    }
}

@Composable
private fun AddressCardContent(address: Address, modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.XXSmall)
    ) {
        Text(
            text = "Endereço selecionado",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = address.toPrimaryFormat(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = address.toSecondaryFormat(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AddressCardActions(onEdit: () -> Unit, onRemove: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.XXSmall)) {
        AddressActionButton(
            icon = Icons.Outlined.Edit,
            tint = MaterialTheme.colorScheme.primary,
            onClick = onEdit
        )
        AddressActionButton(
            icon = Icons.Outlined.Delete,
            tint = MaterialTheme.colorScheme.error,
            onClick = onRemove
        )
    }
}

@Composable
private fun AddressActionButton(icon: ImageVector, tint: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(ComponentSize.Small)
            .clip(RoundedCornerShape(CornerRadius.Medium))
            .background(tint.copy(alpha = Alpha.XXLow))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(IconSize.Small)
        )
    }
}

@Composable
private fun SubmitButton(visible: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(tween(220, easing = FastOutSlowInEasing)) + fadeIn(tween(220, easing = FastOutSlowInEasing)),
        exit = shrinkVertically(tween(150)) + fadeOut(tween(150))
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Large),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.Large, horizontal = Spacing.Large)
            ) {
                Text(
                    text = "Finalizar Cadastro",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .size(IconSize.Default)
                        .align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun LocationPermissionHandler(onGranted: () -> Unit) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) onGranted()
    }
    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) permissionsState.launchMultiplePermissionRequest()
    }

    if (permissionsState.shouldShowRationale) {
        LocationPermissionDialog(onConfirm = permissionsState::launchMultiplePermissionRequest)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CameraPermissionHandler(onGranted: () -> Unit) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) onGranted()
    }
    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) permissionState.launchPermissionRequest()
    }

    if (permissionState.status.shouldShowRationale) {
        CameraPermissionDialog(onConfirm = permissionState::launchPermissionRequest)
    }
}

@Composable
private fun LocationPermissionDialog(onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onConfirm,
        title = {
            Text(
                text = stringResource(R.string.permission_location_error_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.permission_location_error_body),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            StandardTextButton(text = "Permitir", onClick = onConfirm, enabled = true)
        }
    )
}

@Composable
private fun CameraPermissionDialog(onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onConfirm,
        title = {
            Text(
                text = "Permissão da Câmera",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "A câmera é necessária para fotografar o imóvel durante a vistoria.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            StandardTextButton(text = "Permitir", onClick = onConfirm, enabled = true)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationSearchSheet(state: RegisterState, events: RegisterEvents) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var query by rememberSaveable { mutableStateOf("") }

    val onAddressSelectedAnimated: (Address) -> Unit = { address ->
        scope.launch {
            sheetState.hide()
            events.onAddressSelected(address)
        }
    }

    ModalBottomSheet(
        onDismissRequest = events.onDismissLocationSheet,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = CornerRadius.XXLarge, topEnd = CornerRadius.XXLarge),
        dragHandle = { SheetDragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.Medium, bottom = Spacing.XXLarge)
        ) {
            Box(modifier = Modifier.padding(horizontal = Spacing.XXLarge)) {
                LocationSearchBar(
                    query = query,
                    onQueryChange = {
                        query = it
                        events.onLocationSearch(it)
                    }
                )
            }

            val hasContent =
                query.isNotEmpty() || state.isSearching || state.isFetchingLocation || state.addresses.isNotEmpty()

            AnimatedVisibility(
                visible = hasContent,
                enter = fadeIn(tween(220, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(150))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(Spacing.Large))
                    SearchResultsContent(
                        state = state,
                        onAddressSelected = onAddressSelectedAnimated
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetDragHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = Spacing.Medium)
            .size(width = ComponentSize.Small, height = Spacing.XXSmall)
            .clip(RoundedCornerShape(Spacing.XXSmall))
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = Alpha.Medium))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationSearchBar(query: String, onQueryChange: (String) -> Unit) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                placeholder = {
                    Text(
                        text = "Digite sua localização",
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        },
        expanded = false,
        onExpandedChange = {},
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {}
}

private sealed interface SearchResultState {
    data object Idle : SearchResultState
    data object Loading : SearchResultState
    data object Empty : SearchResultState
    data class Results(val addresses: List<Address>) : SearchResultState
}

@Composable
private fun SearchResultsContent(state: RegisterState, onAddressSelected: (Address) -> Unit) {
    val resultState = when {
        state.isSearching || state.isFetchingLocation -> SearchResultState.Loading
        state.addresses.isNotEmpty() -> SearchResultState.Results(state.addresses.toList())
        state.searchQuery.isNotEmpty() -> SearchResultState.Empty
        else -> SearchResultState.Idle
    }

    AnimatedContent(
        targetState = resultState,
        transitionSpec = {
            (fadeIn(tween(220, easing = FastOutSlowInEasing)) +
                    scaleIn(tween(220, easing = FastOutSlowInEasing), initialScale = 0.96f)) togetherWith
                    (fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.96f))
        },
        label = "search_results"
    ) { searchState ->
        when (searchState) {
            SearchResultState.Loading -> LoadingState()
            SearchResultState.Empty -> EmptySearchState()
            is SearchResultState.Results -> AddressResultsList(
                addresses = searchState.addresses,
                onSelect = onAddressSelected
            )
            SearchResultState.Idle -> Spacer(modifier = Modifier.height(Spacing.XSmall))
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.XXHuge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Large)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(ComponentSize.Small),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = Spacing.XSmall
        )
        Text(
            text = "Buscando endereços...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.Huge, horizontal = Spacing.XXLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Large)
    ) {
        Box(
            modifier = Modifier
                .size(ComponentSize.XLarge)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = Alpha.XLow),
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = Alpha.XXLow)
                        )
                    )
                )
                .border(
                    width = Spacing.Hairline,
                    color = MaterialTheme.colorScheme.error.copy(alpha = Alpha.XXLow),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error.copy(alpha = Alpha.High),
                modifier = Modifier.size(IconSize.XLarge)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
        ) {
            Text(
                text = "Nenhum resultado encontrado",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tente buscar com outros termos ou verifique a ortografia",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AddressResultsList(addresses: List<Address>, onSelect: (Address) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = Spacing.Medium)
    ) {
        addresses.forEachIndexed { index, address ->
            AddressResultItem(
                address = address,
                isLast = index == addresses.lastIndex,
                onClick = { onSelect(address) }
            )
        }
    }
}

@Composable
private fun AddressResultItem(address: Address, isLast: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = Spacing.XXLarge, vertical = Spacing.Large),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AddressResultIcon()
            AddressResultDetails(modifier = Modifier.weight(1f), address = address)
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 80.dp, end = Spacing.XXLarge),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = Alpha.XXLow)
            )
        }
    }
}

@Composable
private fun AddressResultIcon() {
    Box(
        modifier = Modifier
            .size(ComponentSize.Small)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(IconSize.Default)
        )
    }
}

@Composable
private fun AddressResultDetails(modifier: Modifier, address: Address) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.XSmall)) {
        Text(
            text = address.toPrimaryFormat(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = address.toSecondaryFormat(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun Address.toPrimaryFormat(): String = when {
    street.isNotBlank() && neighborhood.isNotBlank() -> "$street, $neighborhood"
    street.isNotBlank() -> street
    neighborhood.isNotBlank() -> neighborhood
    city.isNotBlank() -> city
    else -> "Endereço"
}

private fun Address.toSecondaryFormat(): String = buildList {
    if (number.isNotBlank()) add("N° $number")
    if (postalCode.isNotBlank()) add("CEP $postalCode")
    if (city.isNotBlank()) add(city)
    if (state.isNotBlank()) add(state)
}.joinToString(", ")
