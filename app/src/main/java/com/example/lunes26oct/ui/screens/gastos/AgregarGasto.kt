package com.example.lunes26oct.ui.screens.gastos

import com.example.lunes26oct.domain.model.leeMonto
import kotlinx.coroutines.launch
import com.example.lunes26oct.R
import com.example.lunes26oct.ui.components.IconoGasto
import com.example.lunes26oct.ui.components.IconoCategoria
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto

@Composable
fun AgregarGasto(
    categorias: List<Categoria>,
    onGuardar: suspend (Double, String, Int) -> Boolean,
    onCrearCategoria: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    gastoInicial: Gasto? = null
) {
    val scope = rememberCoroutineScope()
    var guardando by remember { mutableStateOf(false) }
    var monto by rememberSaveable(gastoInicial?.id) { mutableStateOf(gastoInicial?.monto?.toString().orEmpty()) }
    var concepto by rememberSaveable(gastoInicial?.id) { mutableStateOf(gastoInicial?.descripcion.orEmpty()) }
    var categoriaId by rememberSaveable(gastoInicial?.id) { mutableStateOf(gastoInicial?.categoriaId) }
    var menuAbierto by remember { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }
    val seleccionada = categorias.firstOrNull { it.id == categoriaId }
    val forma = RoundedCornerShape(16.dp)

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                TextButton(
                    onClick = onVolver,
                    enabled = !guardando,
                    modifier = Modifier.align(Alignment.CenterStart).semantics { contentDescription = "Volver" },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                    contentPadding = PaddingValues(0.dp)
                ) { IconoGasto(R.drawable.ic_volver) }
                Text(
                    if (gastoInicial == null) "Agregar gasto" else "Editar gasto",
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
                    placeholder = { Text("0.00") },
                    leadingIcon = { IconoGasto(R.drawable.ic_monto) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                            modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) { IconoCategoria(seleccionada?.id, Modifier.size(24.dp)) }
                        Spacer(Modifier.width(12.dp))
                        Text(seleccionada?.nombre ?: if (categorias.isEmpty()) "Sin categorías registradas" else "Selecciona una categoría", modifier = Modifier.weight(1f))
                        IconoGasto(R.drawable.ic_desplegar)
                    }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                leadingIcon = { IconoCategoria(categoria.id, Modifier.size(24.dp)) },
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
                Text("Concepto (opcional)", fontWeight = FontWeight.SemiBold)
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
            if (categorias.isEmpty()) {
                Text("Primero registra una categoria para poder guardar un gasto.")
                TextButton(onClick = onCrearCategoria, enabled = !guardando) { Text("Crear categoria") }
            }
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
            Button(
                onClick = {
                    val valor = leeMonto(monto)
                    val categoria = seleccionada
                    error = when {
                        valor == null -> "Ingresa un monto mayor que cero, con centavos."
                        categoria == null -> "Elige una categoria."
                        else -> ""
                    }
                    if (valor != null && categoria != null) {
                        guardando = true
                        scope.launch {
                            try {
                                if (!onGuardar(valor, concepto.trim(), categoria.id)) {
                                    error = "No se pudo guardar el gasto."
                                }
                            } finally { guardando = false }
                        }
                    }
                },
                enabled = !guardando && categorias.isNotEmpty(),
                shape = forma,
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp).heightIn(min = 56.dp)
            ) { Text(if (gastoInicial == null) "Guardar gasto" else "Guardar cambios", fontWeight = FontWeight.SemiBold) }
        }
    }
}
