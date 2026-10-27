package com.example.lunes26oct.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.R
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.ui.util.formatoMonto

@Composable
internal fun FilaGasto(
    gasto: Gasto,
    nombreCategoria: String,
    onEditar: (Gasto) -> Unit,
    onEliminar: (Gasto) -> Unit
) {
    var menuAbierto by remember(gasto.id) { mutableStateOf(false) }
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
            Box {
                IconButton(
                    onClick = { menuAbierto = true },
                    modifier = Modifier.semantics { contentDescription = "Opciones del gasto ${gasto.descripcion.orEmpty()}" }
                ) { Text("⋮", fontSize = 26.sp) }
                DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, onClick = {
                        menuAbierto = false
                        onEditar(gasto)
                    })
                    DropdownMenuItem(text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) }, onClick = {
                        menuAbierto = false
                        onEliminar(gasto)
                    })
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
