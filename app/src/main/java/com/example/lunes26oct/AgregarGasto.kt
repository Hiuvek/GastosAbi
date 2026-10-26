package com.example.lunes26oct

import androidx.compose.foundation.layout.*
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

private val PaletaGastos = lightColorScheme(
    primary = Color(0xFF7E49B3),
    onPrimary = Color.White,
    background = Color(0xFFCFCFCF),
    onBackground = Color(0xFF141414),
    surface = Color(0xFFCFCFCF),
    onSurface = Color(0xFF141414),
    surfaceVariant = Color(0xFFCFCFCF),
    onSurfaceVariant = Color(0xFF141414),
    outline = Color(0xFF141414)
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
    val forma = RoundedCornerShape(12.dp)

    MaterialTheme(colorScheme = PaletaGastos) {
        Surface(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onVolver,
                        modifier = Modifier.semantics { contentDescription = "Volver" },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("←", style = MaterialTheme.typography.headlineSmall) }
                    Text("Agregar gasto", style = MaterialTheme.typography.titleLarge)
                }
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    placeholder = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = forma,
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Monto" }
                )
                OutlinedTextField(
                    value = concepto,
                    onValueChange = { concepto = it },
                    placeholder = { Text("Concepto") },
                    singleLine = true,
                    shape = forma,
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Concepto" }
                )
                Box {
                    OutlinedButton(
                        onClick = { menuAbierto = true },
                        enabled = categorias.isNotEmpty(),
                        shape = forma,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    ) {
                        Text(seleccionada?.nombre ?: if (categorias.isEmpty()) "Sin categorías registradas" else "Categoría", modifier = Modifier.weight(1f))
                        Text("▾")
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
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp).width(180.dp).heightIn(min = 48.dp)
                ) { Text("Guardar") }
            }
        }
    }
}
