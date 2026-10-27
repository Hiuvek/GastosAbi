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
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun BarraInicio(
    agregando: Boolean,
    viendoLista: Boolean,
    onInicio: () -> Unit,
    onAgregar: () -> Unit,
    onGastos: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp) {
        Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().height(80.dp)) {
            TextButton(
                onClick = onInicio,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = if (agregando || viendoLista) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(R.drawable.ic_inicio), colorFilter = ColorFilter.tint(if (agregando || viendoLista) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary), contentDescription = null, modifier = Modifier.size(26.dp))
                    Text("Inicio")
                }
            }
            TextButton(
                onClick = onGastos,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (viendoLista && !agregando) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painterResource(R.drawable.ic_gastos),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(if (viendoLista && !agregando) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.size(26.dp)
                    )
                    Text("Gastos")
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
