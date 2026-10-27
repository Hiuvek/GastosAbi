package com.example.lunes26oct.ui.screens.gastos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.R
import com.example.lunes26oct.ui.components.FilaGasto
import com.example.lunes26oct.ui.util.formatoMonto
import java.math.BigDecimal

@Composable
fun ListaGastos(
    gastos: List<Gasto>,
    categorias: List<Categoria>,
    onEditar: (Gasto) -> Unit,
    onEliminar: (Gasto) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    titulo: String = "Lista de gastos"
) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    val nombres = categorias.associate { it.id to it.nombre }
    val visibles = gastos.filter {
        busqueda.isBlank() ||
            it.descripcion.orEmpty().contains(busqueda.trim(), ignoreCase = true) ||
            nombres[it.categoriaId].orEmpty().contains(busqueda.trim(), ignoreCase = true)
    }
    val total = visibles.fold(BigDecimal.ZERO) { suma, gasto -> suma + gasto.monto.toBigDecimal() }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Image(
                    painterResource(R.drawable.ic_volver), contentDescription = "Volver",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary), modifier = Modifier.size(24.dp)
                )
            }
            Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar concepto o categoria") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    TextButton(onClick = { busqueda = "" }) { Text("Limpiar") }
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${visibles.size} gastos", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatoMonto(total), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (visibles.isEmpty()) {
                item {
                    Text(
                        if (gastos.isEmpty()) "Todavia no hay gastos. Usa + para registrar el primero."
                        else "No hay gastos que coincidan con tu busqueda."
                    )
                }
            }
            items(visibles, key = { it.id }) { gasto ->
                FilaGasto(gasto, nombres[gasto.categoriaId] ?: "Categoria", onEditar, onEliminar)
            }
        }
    }
}
