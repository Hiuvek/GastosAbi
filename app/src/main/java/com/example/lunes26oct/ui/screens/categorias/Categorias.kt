package com.example.lunes26oct.ui.screens.categorias

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.ui.components.IconoCategoria
import com.example.lunes26oct.R
import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.ui.util.formatoMonto
import java.math.BigDecimal

@Composable
fun Categorias(
    categorias: List<Categoria>,
    gastos: List<Gasto>,
    onVolver: () -> Unit,
    onAgregar: () -> Unit,
    onCategoria: (Categoria) -> Unit,
    onEditar: (Categoria) -> Unit,
    onEliminar: (Categoria) -> Unit,
    modifier: Modifier = Modifier
) {
    val relacionados = gastos.groupBy { it.categoriaId }
    Column(modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Image(painterResource(R.drawable.ic_volver), "Volver a inicio", Modifier.size(24.dp), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary))
            }
            Text("Categorias", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = onAgregar) {
                Image(painterResource(R.drawable.ic_agregar), "Agregar categoria", Modifier.size(24.dp), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary))
            }
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
            if (categorias.isEmpty()) {
                item { Text("No hay categorias registradas. Pulsa + para agregar una.") }
            }
            items(categorias, key = { it.id }) { categoria ->
                var menuAbierto by remember(categoria.id) { mutableStateOf(false) }
                val registros = relacionados[categoria.id].orEmpty()
                val total = registros.fold(BigDecimal.ZERO) { suma, gasto -> suma + gasto.monto.toBigDecimal() }
                Column {
                    Row(
                        Modifier.fillMaxWidth().clickable { onCategoria(categoria) }.padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box {
                            IconButton(
                                onClick = { menuAbierto = true },
                                modifier = Modifier.semantics { contentDescription = "Opciones de ${categoria.nombre}" }
                            ) { Text("⋮", fontSize = 26.sp) }
                            DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                                DropdownMenuItem(text = { Text("Editar") }, onClick = {
                                    menuAbierto = false
                                    onEditar(categoria)
                                })
                                DropdownMenuItem(text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) }, onClick = {
                                    menuAbierto = false
                                    onEliminar(categoria)
                                })
                            }
                        }
                        Box(Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                            IconoCategoria(categoria.id)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(categoria.nombre, fontWeight = FontWeight.SemiBold)
                            Text("${registros.size} ${if (registros.size == 1) "gasto" else "gastos"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(formatoMonto(total), fontWeight = FontWeight.Bold)
                        Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}
