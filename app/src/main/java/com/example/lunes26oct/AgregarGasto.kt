package com.example.lunes26oct

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.ui.theme.Categoria

@Composable
fun AgregarGasto(
    categorias: List<Categoria>,
    onCrearCategoria: (String) -> Categoria?,
    onGuardar: (Long, String, Int) -> Boolean,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var monto by rememberSaveable { mutableStateOf("") }
    var concepto by rememberSaveable { mutableStateOf("") }
    var categoriaId by rememberSaveable { mutableStateOf<Int?>(null) }
    var menuAbierto by remember { mutableStateOf(false) }
    var nuevaCategoria by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }
    val seleccionada = categorias.firstOrNull { it.id == categoriaId }

    Column(
        modifier = modifier.padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Agregar gasto", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it },
            label = { Text("Monto") },
            supportingText = { Text("Ingresa un número entero mayor que cero.") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = concepto,
            onValueChange = { concepto = it },
            label = { Text("Concepto") },
            modifier = Modifier.fillMaxWidth()
        )
        Column {
            Text("Categoría")
            OutlinedButton(onClick = { menuAbierto = true }, enabled = categorias.isNotEmpty()) {
                Text(seleccionada?.nombre ?: "Elegir categoría")
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
        if (categorias.isEmpty()) Text("Crea una categoría para registrar tu primer gasto.")
        OutlinedTextField(
            value = nuevaCategoria,
            onValueChange = { nuevaCategoria = it },
            label = { Text("Nueva categoría") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        TextButton(onClick = {
            val creada = onCrearCategoria(nuevaCategoria.trim())
            if (creada == null) {
                error = "Escribe un nombre válido que no esté registrado."
            } else {
                categoriaId = creada.id
                nuevaCategoria = ""
                error = ""
            }
        }) { Text("Crear categoría") }
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        Button(onClick = {
            val valor = monto.trim().toLongOrNull()
            val categoria = seleccionada
            error = when {
                valor == null || valor <= 0L -> "Ingresa un monto entero válido mayor que cero."
                concepto.isBlank() -> "Escribe el concepto del gasto."
                categoria == null -> "Elige una categoría."
                !onGuardar(valor, concepto.trim(), categoria.id) -> "No se pudo guardar el gasto. Inténtalo nuevamente."
                else -> ""
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Guardar gasto") }
        TextButton(onClick = onVolver) { Text("Volver") }
    }
}
