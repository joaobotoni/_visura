package com.visura.ui.presenter.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateListOf
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

import com.visura.domain.vo.imovel.Imovel
import kotlinx.coroutines.launch
enum class ImovelSortType {
    ID, TIPO_IMOVEL, DATA_CRIACAO
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val listaImoveis = remember {
        mutableStateListOf(
            Imovel(
                id = 5292,
                tipoImovel = "Casa",
                criadoEm = "15/09/2025",
                cep = "78020-400",
                rua = "Comandante Costa",
                numero = 1758,
                bairro = "Centro-Sul",
                cidade = "Cuiabá",
                estado = "MT",
                complemento = "Villaggio Pompéia ao 1804"
            ),
            Imovel(
                5293,
                "Casa",
                "16/09/2025",
                "78045-300",
                "Avenida Getúlio Vargas",
                900,
                "Goiabeiras",
                "Cuiabá",
                "MT"
            ),
            Imovel(
                5294,
                "Apartamento",
                "17/09/2025",
                "78049-900",
                "Rua das Palmeiras",
                101,
                "Jardim Cuiabá",
                "São Paulo",
                "SP"
            ),
            Imovel(
                5295,
                "Casa",
                "18/09/2025",
                "78048-000",
                "Rua do Sol",
                205,
                "Santa Rosa",
                "Cuiabá",
                "MT"
            ),
            Imovel(
                5296,
                "Chácara",
                "19/09/2025",
                "78030-000",
                "Avenida Histórica",
                55,
                "Porto",
                "Cuiabá",
                "MT"
            ),
            Imovel(
                5297,
                "Prédio comercial",
                "20/09/2025",
                "78035-000",
                "Rua da Paz",
                800,
                "Alvorada",
                "Cuiabá",
                "MT"
            ),
            Imovel(
                5298,
                "Terreno",
                "21/09/2025",
                "78040-000",
                "Rua Principal",
                123,
                "Pedregal",
                "Cuiabá",
                "MT"
            ),
        )
    }

    var imoveisOrdenados by remember { mutableStateOf(listaImoveis.toList().sortedBy { it.id }) }
    var sortType by remember { mutableStateOf(ImovelSortType.ID) }
    var expandedSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(sortType, listaImoveis.size) {
        val listaBase = listaImoveis.toList()
        imoveisOrdenados = when (sortType) {
            ImovelSortType.ID -> listaBase.sortedBy { it.id }
            ImovelSortType.TIPO_IMOVEL -> listaBase.sortedBy { it.tipoImovel }
            ImovelSortType.DATA_CRIACAO -> listaBase.sortedBy { it.criadoEm }
        }
        if (imoveisOrdenados.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    val onEditClick: (Imovel) -> Unit = { imovel ->
        Log.d("IMOBILIARIA_APP", "EDITAR Imóvel: ${imovel.id}")
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                "Preparando para editar Imóvel ${imovel.id}...",
                duration = SnackbarDuration.Short
            )
        }
    }

    val onDeleteClick: (Imovel) -> Unit = { imovel ->
        coroutineScope.launch {
            val itemRemoved = listaImoveis.removeIf { it.id == imovel.id }
            if (itemRemoved) {
                Log.d("IMOBILIARIA_APP", "EXCLUIR Imóvel: ${imovel.id} (Removido do estado)")
                snackbarHostState.showSnackbar(
                    "Imóvel ${imovel.id} excluído.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    val onImovelClick: (Imovel) -> Unit = { imovelSelecionado ->
        Log.d("IMOBILIARIA_APP", "DETALHES Imóvel: ${imovelSelecionado.id}")
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = "Imóvel ${imovelSelecionado.id} selecionado: ${imovelSelecionado.rua}",
                actionLabel = "VER",
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
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
                Text(
                    text = "Ordenar por:",
                    fontWeight = FontWeight.SemiBold
                )
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
                        ImovelSortType.entries.forEach { sortOption ->
                            DropdownMenuItem(
                                text = { Text(sortOption.name.replace('_', ' ')) },
                                onClick = {
                                    sortType = sortOption
                                    expandedSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            if (imoveisOrdenados.isEmpty()) {
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
                        items = imoveisOrdenados,
                        key = { it.id }
                    ) { imovel ->
                        ImovelItem(
                            imovel = imovel,
                            onClick = onImovelClick,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick
                        )
                    }
                }
            }
        }
    }
}
