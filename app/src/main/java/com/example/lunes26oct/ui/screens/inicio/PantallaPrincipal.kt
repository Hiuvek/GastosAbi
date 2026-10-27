package com.example.lunes26oct.ui.screens.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.domain.model.ResumenCategoria
import java.math.BigDecimal

import com.example.lunes26oct.ui.components.FilaGasto
import com.example.lunes26oct.ui.util.formatoMonto

@Composable
fun PantallaPrincipal(
    gastos: List<Gasto>,
    resumen: List<ResumenCategoria>,
    categorias: List<Categoria>,
    onEditar: (Gasto) -> Unit,
    onEliminar: (Gasto) -> Unit,
    mensaje: String,
    uidDispositivo: String = "",
    modifier: Modifier = Modifier
) {
    val total = resumen.fold(BigDecimal.ZERO) { suma, categoria -> suma + categoria.total.toBigDecimal() }
    val nombres = categorias.associate { it.id to it.nombre }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Hola, bienvenido.",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (uidDispositivo.isNotBlank()) {
                    Text(
                        "UID: $uidDispositivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Gasto total registrado")
                    Text(formatoMonto(total), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DatoInicio("Gastos", gastos.size.toString(), Modifier.weight(1f))
                DatoInicio("Categorias utilizadas", gastos.map { it.categoriaId }.distinct().size.toString(), Modifier.weight(1f))
            }
        }
        if (mensaje.isNotEmpty()) item { Text(mensaje, color = MaterialTheme.colorScheme.primary) }
        item { Text("Resumen por categoria", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        if (resumen.isEmpty()) {
            item { Text("Todavia no hay categorias. Entra a Categorias y pulsa + para crear una.") }
        }
        items(resumen, key = { "resumen_${it.nombre}" }) { categoria ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(categoria.nombre, Modifier.weight(1f))
                Text(formatoMonto(categoria.total.toBigDecimal()), fontWeight = FontWeight.SemiBold)
            }
        }
        item {
            Text("Tus ultimos gastos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
        if (gastos.isEmpty()) {
            item {
                Text("Todavia no hay gastos registrados. Usa el boton + para agregar uno.")
            }
        }
        // El modelo consulta por id DESC: los primeros son los registros mas recientes.
        items(gastos.take(10), key = { it.id }) { gasto ->
            FilaGasto(gasto, nombres[gasto.categoriaId] ?: "Categoria", onEditar, onEliminar)
        }
    }
}

@Composable
private fun DatoInicio(titulo: String, valor: String, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, style = MaterialTheme.typography.bodyMedium)
            Text(valor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
