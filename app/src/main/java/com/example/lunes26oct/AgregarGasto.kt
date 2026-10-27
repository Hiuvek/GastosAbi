package com.example.lunes26oct

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.ui.theme.Categoria

internal val PaletaGastos = lightColorScheme(
    primary = Color(0xFF804CB4),
    onPrimary = Color.White,
    background = Color(0xFFCFCFCF),
    onBackground = Color(0xFF141414),
    surface = Color(0xFFCFCFCF),
    onSurface = Color(0xFF141414),
    surfaceVariant = Color(0xFFCFCFCF),
    onSurfaceVariant = Color(0xFF141414),
    outline = Color(0xFFB7B7B7)
)

@Composable
fun AgregarGasto(
    categorias: List<Categoria>,
    onGuardar: (Long, String, Int) -> Boolean,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var monto by rememberSaveable { mutableStateOf("") }
    var concepto by rememberSaveable { mutableStateOf("") }
    var categoriaId by rememberSaveable { mutableStateOf<Int?>(null) }
    var menuAbierto by remember { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }
    val seleccionada = categorias.firstOrNull { it.id == categoriaId }
    val forma = RoundedCornerShape(16.dp)

    MaterialTheme(colorScheme = PaletaGastos) {
        Surface(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    TextButton(
                        onClick = onVolver,
                        modifier = Modifier.align(Alignment.CenterStart).semantics { contentDescription = "Volver" },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                        contentPadding = PaddingValues(0.dp)
                    ) { IconoGasto(R.drawable.ic_volver) }
                    Text(
                        "Agregar gasto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Monto", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        placeholder = { Text("0") },
                        leadingIcon = { IconoGasto(R.drawable.ic_monto) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = forma,
                        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Monto" }
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Categoría", fontWeight = FontWeight.SemiBold)
                    Box {
                        OutlinedButton(
                            onClick = { menuAbierto = true },
                            enabled = categorias.isNotEmpty(),
                            shape = forma,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).background(Color(0xFFD1B8EC), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) { IconoGasto(R.drawable.ic_categoria) }
                            Spacer(Modifier.width(12.dp))
                            Text(seleccionada?.nombre ?: if (categorias.isEmpty()) "Sin categorías registradas" else "Selecciona una categoría", modifier = Modifier.weight(1f))
                            IconoGasto(R.drawable.ic_desplegar)
                        }
                        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                            categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.nombre) },
                                    onClick = {
                                        categoriaId = categoria.id
                                        menuAbierto = false
                                    }
                                )
                            }

                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Concepto", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = concepto,
                        onValueChange = { concepto = it },
                        placeholder = { Text("¿En qué gastaste?") },
                        leadingIcon = { IconoGasto(R.drawable.ic_concepto) },
                        singleLine = true,
                        shape = forma,
                        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Concepto" }
                    )
                }
                if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
                Button(
                    onClick = {
                        val valor = monto.trim().toLongOrNull()
                        val categoria = seleccionada
                        error = when {
                            valor == null || valor <= 0L -> "Ingresa un monto entero mayor que cero."
                            concepto.isBlank() -> "Escribe el concepto del gasto."
                            categoria == null -> "Elige una categoría."
                            !onGuardar(valor, concepto.trim(), categoria.id) -> "No se pudo guardar el gasto."
                            else -> ""
                        }
                    },
                    shape = forma,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp).heightIn(min = 56.dp)
                ) { Text("Guardar gasto", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

// Reemplaza los PNG de res/drawable conservando sus nombres.
@Composable
private fun IconoGasto(@DrawableRes recurso: Int) {
    Image(
        painter = painterResource(recurso),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}
