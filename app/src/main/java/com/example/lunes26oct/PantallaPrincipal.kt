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
import androidx.compose.ui.graphics.Color
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3D5EF)),
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
                DatoInicio("Gastos", gastos.size.toString(), Color(0xFFEBDCDF), Modifier.weight(1f))
                DatoInicio("Categorias utilizadas", gastos.map { it.categoriaId }.distinct().size.toString(), Color(0xFFDDD5E6), Modifier.weight(1f))
            }
        }
        if (mensaje.isNotEmpty()) item { Text(mensaje, color = MaterialTheme.colorScheme.primary) }
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
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(44.dp).background(Color(0xFFE3D5EF), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painterResource(R.drawable.ic_categoria), contentDescription = null, modifier = Modifier.size(26.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(nombres[gasto.categoriaId] ?: "Categoria", fontWeight = FontWeight.SemiBold)
                        if (!gasto.descripcion.isNullOrBlank()) {
                            Text(gasto.descripcion, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Text(formatoMonto(gasto.monto.toBigDecimal()), fontWeight = FontWeight.SemiBold)
                }
                HorizontalDivider(color = Color(0xFFB7B7B7))
            }
        }
    }
}

@Composable
private fun DatoInicio(titulo: String, valor: String, fondo: Color, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = fondo)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, style = MaterialTheme.typography.bodyMedium)
            Text(valor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatoMonto(monto: BigDecimal): String = "$ " + NumberFormat.getIntegerInstance().format(monto)

@Composable
fun BarraInicio(agregando: Boolean, onInicio: () -> Unit, onAgregar: () -> Unit) {
    Surface(color = Color(0xFFE3D5EF), shadowElevation = 6.dp) {
        Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().height(80.dp)) {
            TextButton(
                onClick = onInicio,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = if (agregando) Color(0xFF141414) else Color(0xFF7E49B3))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(R.drawable.ic_inicio), contentDescription = null, modifier = Modifier.size(26.dp))
                    Text("Inicio")
                }
            }
            FilledIconButton(
                onClick = onAgregar,
                modifier = Modifier.align(Alignment.Center).size(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7E49B3))
            ) {
                Image(painterResource(R.drawable.ic_agregar), contentDescription = "Agregar gasto", modifier = Modifier.size(28.dp))
            }
        }
    }
}
