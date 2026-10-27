package com.example.lunes26oct.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lunes26oct.R

internal val LocalIconosCategoria = staticCompositionLocalOf<Map<Int, String>> { emptyMap() }

internal enum class OpcionIconoCategoria(val clave: String, val nombre: String, @get:DrawableRes val recurso: Int) {
    CARRITO("carrito", "Carrito", R.drawable.ic_categoria),
    TRANSPORTE("transporte", "Transporte", R.drawable.ic_categoria_transporte),
    COMIDA("comida", "Comida", R.drawable.ic_categoria_comida),
    ENTRETENIMIENTO("entretenimiento", "Entretenimiento", R.drawable.ic_categoria_entretenimiento),
    ROPA("ropa", "Ropa", R.drawable.ic_categoria_ropa),
    SALUD("salud", "Salud", R.drawable.ic_categoria_salud),
    OTROS("otros", "Otros", R.drawable.ic_categoria_otros);

    companion object {
        fun desdeClave(clave: String?) = entries.firstOrNull { it.clave == clave } ?: CARRITO
    }
}

@Composable
internal fun IconoCategoria(categoriaId: Int?, modifier: Modifier = Modifier.size(28.dp)) {
    val opcion = OpcionIconoCategoria.desdeClave(LocalIconosCategoria.current[categoriaId])
    ImagenOpcionCategoria(opcion, modifier)
}

@Composable
private fun ImagenOpcionCategoria(opcion: OpcionIconoCategoria, modifier: Modifier) {
    Image(
        painterResource(opcion.recurso),
        contentDescription = null,
        modifier = modifier,
        // Los iconos proporcionados conservan sus colores originales.
        colorFilter = if (opcion == OpcionIconoCategoria.CARRITO) ColorFilter.tint(MaterialTheme.colorScheme.primary) else null
    )
}

@Composable
internal fun SelectorIconoCategoria(seleccionado: String, onSeleccionar: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Icono de la categoria", style = MaterialTheme.typography.labelLarge)
        OpcionIconoCategoria.entries.chunked(4).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                fila.forEach { opcion ->
                    val activo = seleccionado == opcion.clave
                    Column(
                        modifier = Modifier.weight(1f)
                            .selectable(selected = activo, role = Role.RadioButton, onClick = { onSeleccionar(opcion.clave) })
                            .border(if (activo) 2.dp else 1.dp, if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .padding(horizontal = 2.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ImagenOpcionCategoria(opcion, Modifier.size(30.dp))
                        Text(opcion.nombre, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                repeat(4 - fila.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
