package com.example.lunes26oct.ui.screens.categorias

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.domain.model.Categoria

@Composable
fun NuevaCategoria(
    onCerrar: () -> Unit,
    onGuardar: (String) -> Boolean,
    categoriaInicial: Categoria? = null
) {
    var nombre by rememberSaveable(categoriaInicial?.id) { mutableStateOf(categoriaInicial?.nombre.orEmpty()) }
    var error by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (categoriaInicial == null) "Nueva categoria" else "Editar categoria") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = "" },
                    placeholder = { Text("Nombre de la categoria") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    isError = error.isNotEmpty()
                )
                if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                error = when {
                    nombre.isBlank() -> "Escribe un nombre."
                    !onGuardar(nombre.trim()) -> "No se pudo guardar. Revisa que el nombre no este registrado."
                    else -> ""
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
