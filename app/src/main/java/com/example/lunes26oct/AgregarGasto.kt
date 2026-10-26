package com.example.lunes26oct

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
                    ) { Text("←", fontSize = 26.sp) }
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
                        leadingIcon = { Text("$", fontSize = 24.sp) },
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
                            ) { IconoCategoria() }
                            Spacer(Modifier.width(12.dp))
                            Text(seleccionada?.nombre ?: if (categorias.isEmpty()) "Sin categorías registradas" else "Selecciona una categoría", modifier = Modifier.weight(1f))
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
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Concepto", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = concepto,
                        onValueChange = { concepto = it },
                        placeholder = { Text("¿En qué gastaste?") },
                        leadingIcon = { Text("T", fontSize = 24.sp) },
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

@Composable
private fun IconoCategoria() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val color = Color(0xFF7E49B3)
        val grosor = 2.dp.toPx()
        fun punto(x: Float, y: Float) = Offset(size.width * x, size.height * y)
        drawLine(color, punto(.08f, .15f), punto(.22f, .15f), grosor)
        drawLine(color, punto(.22f, .15f), punto(.35f, .68f), grosor)
        drawLine(color, punto(.28f, .3f), punto(.9f, .3f), grosor)
        drawLine(color, punto(.9f, .3f), punto(.8f, .57f), grosor)
        drawLine(color, punto(.8f, .57f), punto(.32f, .57f), grosor)
        drawLine(color, punto(.35f, .68f), punto(.8f, .68f), grosor)
        drawCircle(color, 2.dp.toPx(), punto(.4f, .84f))
        drawCircle(color, 2.dp.toPx(), punto(.75f, .84f))
    }
}
