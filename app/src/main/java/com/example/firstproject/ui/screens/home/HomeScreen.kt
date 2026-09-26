package com.example.firstproject.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstproject.ui.theme.BorderGray
import com.example.firstproject.ui.theme.NavyBlue
import com.example.firstproject.ui.theme.TextGray
import java.util.Calendar

private val LightBlueBg = Color(0xFFEAF0FB)
private val StatusOrangeBg = Color(0xFFFDEBD8)
private val StatusOrangeText = Color(0xFFB86A1E)
private val StatusGreenBg = Color(0xFFE1F3E4)
private val StatusGreenText = Color(0xFF2E7D32)

private fun Modifier.cardBorder() = this.border(1.dp, BorderGray, RoundedCornerShape(16.dp))

private fun saudacaoPorHorario(): String {
    val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hora) {
        in 5..11 -> "Bom dia,"
        in 12..17 -> "Boa tarde,"
        else -> "Boa noite,"
    }
}

@Composable
fun HomeScreen(
    nomeUsuario: String = "Marcos Andrade",
    vistorias: List<Vistoria> = vistoriasExemplo,
    onNovaVistoriaClick: () -> Unit = {}
) {
    var abaSelecionada by remember { mutableStateOf(0) } // 0 = Home, 1 = Perfil

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = abaSelecionada == 0,
                    onClick = { abaSelecionada = 0 },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyBlue,
                        selectedTextColor = NavyBlue,
                        indicatorColor = LightBlueBg
                    )
                )
                NavigationBarItem(
                    selected = abaSelecionada == 1,
                    onClick = { abaSelecionada = 1 },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NavyBlue,
                        selectedTextColor = NavyBlue,
                        indicatorColor = LightBlueBg
                    )
                )
            }
        },
        floatingActionButton = {
            if (abaSelecionada == 0) {
                ExtendedFloatingActionButton(
                    onClick = onNovaVistoriaClick,
                    containerColor = NavyBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nova vistoria", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        when (abaSelecionada) {
            0 -> HomeContent(
                nomeUsuario = nomeUsuario,
                vistorias = vistorias,
                modifier = Modifier.padding(padding)
            )
            else -> PerfilPlaceholder(modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun HomeContent(
    nomeUsuario: String,
    vistorias: List<Vistoria>,
    modifier: Modifier = Modifier
) {
    val totais = vistorias.size
    val emAndamento = vistorias.count { it.status == StatusVistoria.EM_ANDAMENTO }
    val concluidas = vistorias.count { it.status == StatusVistoria.CONCLUIDA }

    val iniciais = nomeUsuario
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(saudacaoPorHorario(), fontSize = 14.sp, color = TextGray)
                    Text(nomeUsuario, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NavyBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(iniciais, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard("$totais", "Vistorias totais", Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                StatCard("$emAndamento", "Em andamento", Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                StatCard("$concluidas", "Concluídas", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Suas vistorias", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (vistorias.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma vistoria cadastrada ainda.", color = TextGray)
                }
            }
        } else {
            items(vistorias) { vistoria ->
                VistoriaCard(vistoria)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun StatCard(numero: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(LightBlueBg)
            .padding(vertical = 14.dp, horizontal = 12.dp)
    ) {
        Text(numero, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 12.sp, color = TextGray)
    }
}

@Composable
private fun VistoriaCard(vistoria: Vistoria) {
    val icone = when {
        vistoria.titulo.contains("Edifício", ignoreCase = true) ||
                vistoria.titulo.contains("apto", ignoreCase = true) -> Icons.Filled.Apartment
        vistoria.titulo.contains("Sala", ignoreCase = true) ||
                vistoria.titulo.contains("Comercial", ignoreCase = true) -> Icons.Filled.Storefront
        else -> Icons.Filled.House
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .cardBorder()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icone, contentDescription = null, tint = NavyBlue)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(vistoria.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(vistoria.endereco, fontSize = 13.sp, color = TextGray)
            }

            StatusBadge(vistoria.status)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = vistoria.data,
            fontSize = 12.sp,
            color = TextGray,
            modifier = Modifier.align(Alignment.End)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Apartment,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${vistoria.ambientesFeitos} de ${vistoria.ambientesTotais} ambientes",
                    fontSize = 13.sp,
                    color = TextGray
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(vistoria.responsavel, fontSize = 13.sp, color = TextGray)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: StatusVistoria) {
    val (texto, bg, textColor) = when (status) {
        StatusVistoria.EM_ANDAMENTO -> Triple("Em andamento", StatusOrangeBg, StatusOrangeText)
        StatusVistoria.CONCLUIDA -> Triple("Concluída", StatusGreenBg, StatusGreenText)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(texto, fontSize = 11.sp, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PerfilPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Text("Perfil (em construção)", color = TextGray)
    }
}