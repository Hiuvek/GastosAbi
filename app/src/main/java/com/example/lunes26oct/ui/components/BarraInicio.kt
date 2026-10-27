package com.example.lunes26oct.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lunes26oct.R

@Composable
fun BarraInicio(
    agregando: Boolean,
    viendoLista: Boolean,
    viendoCategorias: Boolean,
    onInicio: () -> Unit,
    onAgregar: () -> Unit,
    onGastos: () -> Unit,
    onCategorias: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(88.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BotonSeccion("Inicio", R.drawable.ic_inicio, !agregando && !viendoLista && !viendoCategorias, onInicio, Modifier.weight(1f))
            BotonSeccion("Gastos", R.drawable.ic_gastos, viendoLista && !agregando, onGastos, Modifier.weight(1f))
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                FilledIconButton(
                    onClick = onAgregar,
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Image(
                        painterResource(R.drawable.ic_agregar),
                        contentDescription = "Agregar gasto",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            BotonSeccion("Categorias", R.drawable.ic_categoria, viendoCategorias && !agregando, onCategorias, Modifier.weight(2f))
        }
    }
}

@Composable
private fun BotonSeccion(texto: String, @DrawableRes icono: Int, seleccionado: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    TextButton(onClick = onClick, modifier = modifier, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(painterResource(icono), contentDescription = null, colorFilter = ColorFilter.tint(color), modifier = Modifier.size(24.dp))
            Text(texto, color = color, fontSize = 12.sp, maxLines = 1)
        }
    }
}
