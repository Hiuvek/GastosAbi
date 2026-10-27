package com.example.lunes26oct

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
import com.example.lunes26oct.ui.theme.Categoria
import com.example.lunes26oct.ui.theme.Gasto
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
fun PantallaPrincipal(
    gastos: List<Gasto>,
    categorias: List<Categoria>,
    mensaje: String,
    onVerGastos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = gastos.fold(BigDecimal.ZERO) { suma, gasto -> suma + gasto.monto.toBigDecimal() }
    val nombres = categorias.associate { it.id to it.nombre }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text("Hola, bienvenido.", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
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
        item {
            Column {
                Text("Tus ultimos gastos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onVerGastos) { Text("Ver todos") }
            }
        }
        if (gastos.isEmpty()) {
            item {
                Text("Todavia no hay gastos registrados. Usa el boton + para agregar uno.")
            }
        }
        // El modelo consulta por id DESC: los primeros son los registros mas recientes.
        items(gastos.take(10), key = { it.id }) { gasto ->
            FilaGasto(gasto, nombres[gasto.categoriaId] ?: "Categoria")
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

internal fun formatoMonto(monto: BigDecimal): String = "$ " + NumberFormat.getIntegerInstance().format(monto)

@Composable
fun BarraInicio(agregando: Boolean, onInicio: () -> Unit, onAgregar: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp) {
        Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().height(80.dp)) {
            TextButton(
                onClick = onInicio,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = if (agregando) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(R.drawable.ic_inicio), colorFilter = ColorFilter.tint(if (agregando) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary), contentDescription = null, modifier = Modifier.size(26.dp))
                    Text("Inicio")
                }
            }
            FilledIconButton(
                onClick = onAgregar,
                modifier = Modifier.align(Alignment.Center).size(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Image(painterResource(R.drawable.ic_agregar), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary), contentDescription = "Agregar gasto", modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
internal fun FilaGasto(gasto: Gasto, nombreCategoria: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.ic_categoria), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary), contentDescription = null, modifier = Modifier.size(26.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(nombreCategoria, fontWeight = FontWeight.SemiBold)
                if (!gasto.descripcion.isNullOrBlank()) {
                    Text(gasto.descripcion, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(formatoMonto(gasto.monto.toBigDecimal()), fontWeight = FontWeight.SemiBold)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
