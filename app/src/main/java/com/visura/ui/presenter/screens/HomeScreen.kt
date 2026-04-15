package com.visura.ui.presenter.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visura.domain.vo.location.Address
import com.visura.domain.vo.property.Property
import com.visura.domain.vo.property.PropertyCategory
import com.visura.domain.vo.property.PropertyType
import com.visura.ui.presenter.screens.property.PropertyCard
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

enum class PropertySortType {
    ID, TIPO, DATA_CRIACAO
}

@Composable
fun Home() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val properties = remember {
        mutableListOf(
            Property(
                id = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
                type = PropertyType.RESIDENTIAL,
                category = PropertyCategory.HOME,
                address = Address(
                    street = "Rua Comandante Costa",
                    number = "1758",
                    complement = "Villaggio Pompéia",
                    neighborhood = "Centro-Sul",
                    city = "Cuiabá",
                    state = "MT",
                    country = "Brasil",
                    postalCode = "78020-400",
                    latitude = -15.5989,
                    longitude = -56.0949
                ),
                created = Instant.parse("2025-09-15T10:00:00Z")
            ),
            Property(
                id = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901"),
                type = PropertyType.COMMERCIAL,
                category = PropertyCategory.APARTMENT,
                address = Address(
                    street = "Avenida Getúlio Vargas",
                    number = "900",
                    complement = "",
                    neighborhood = "Goiabeiras",
                    city = "Cuiabá",
                    state = "MT",
                    country = "Brasil",
                    postalCode = "78045-300",
                    latitude = -15.5870,
                    longitude = -56.0825
                ),
                created = Instant.parse("2025-09-16T14:30:00Z")
            ),
            Property(
                id = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012"),
                type = PropertyType.NON_RESIDENTIAL,
                category = PropertyCategory.HOME,
                address = Address(
                    street = "Rua das Palmeiras",
                    number = "101",
                    complement = "Bloco B",
                    neighborhood = "Jardim Cuiabá",
                    city = "Cuiabá",
                    state = "MT",
                    country = "Brasil",
                    postalCode = "78049-900",
                    latitude = -15.6012,
                    longitude = -56.1023
                ),
                created = Instant.parse("2025-09-17T09:15:00Z")
            )
        )
    }

    var sortType by remember { mutableStateOf(PropertySortType.ID) }
    var expandedSortMenu by remember { mutableStateOf(false) }
    var propertiesOrdenadas by remember { mutableStateOf(properties.sortedBy { it.id.toString() }) }

    LaunchedEffect(sortType, properties.size) {
        propertiesOrdenadas = when (sortType) {
            PropertySortType.ID -> properties.sortedBy { it.id.toString() }
            PropertySortType.TIPO -> properties.sortedBy { it.type.displayName }
            PropertySortType.DATA_CRIACAO -> properties.sortedBy { it.created }
        }
        if (propertiesOrdenadas.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Ordenar por:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    Button(
                        onClick = { expandedSortMenu = true },
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(sortType.name.replace('_', ' '))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expandedSortMenu,
                        onDismissRequest = { expandedSortMenu = false }
                    ) {
                        PropertySortType.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name.replace('_', ' ')) },
                                onClick = {
                                    sortType = option
                                    expandedSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            if (propertiesOrdenadas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Não há nenhum imóvel cadastrado no momento.",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
                ) {
                    items(
                        items = propertiesOrdenadas,
                        key = { it.id.toString() }
                    ) { property ->
                        PropertyCard(
                            property = property,
                            onClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "${it.type.displayName} em ${it.address.city} selecionado",
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            onEditClick = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Preparando para editar ${it.type.displayName}...",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            onDeleteClick = {
                                properties.removeIf { p -> p.id == it.id }
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        "${it.type.displayName} excluído.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}