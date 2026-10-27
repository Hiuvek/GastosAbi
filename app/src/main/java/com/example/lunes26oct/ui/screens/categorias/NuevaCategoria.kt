package com.example.lunes26oct.ui.screens.categorias

import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import com.example.lunes26oct.ui.components.LocalIconosCategoria
import com.example.lunes26oct.ui.components.OpcionIconoCategoria
import com.example.lunes26oct.ui.components.SelectorIconoCategoria
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.domain.model.Categoria

@Composable
fun NuevaCategoria(
    onCerrar: () -> Unit,
    onGuardar: suspend (String, String) -> Boolean,
    categoriaInicial: Categoria? = null
) {
    val scope = rememberCoroutineScope()
    var guardando by remember { mutableStateOf(false) }
    var nombre by rememberSaveable(categoriaInicial?.id) { mutableStateOf(categoriaInicial?.nombre.orEmpty()) }
    val iconoInicial = OpcionIconoCategoria.desdeClave(LocalIconosCategoria.current[categoriaInicial?.id]).clave
    var icono by rememberSaveable(categoriaInicial?.id) { mutableStateOf(iconoInicial) }
    var error by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { if (!guardando) onCerrar() },
        title = { Text(if (categoriaInicial == null) "Nueva categoria" else "Editar categoria") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = "" },
                    placeholder = { Text("Nombre de la categoria") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    isError = error.isNotEmpty()
                )
                SelectorIconoCategoria(icono) { icono = it }
                if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(enabled = !guardando, onClick = {
                if (nombre.isBlank()) error = "Escribe un nombre."
                else {
                    guardando = true
                    scope.launch {
                        try {
                            if (!onGuardar(nombre.trim(), icono)) {
                                error = "No se pudo guardar. Revisa que el nombre no este registrado."
                            }
                        } finally { guardando = false }
                    }
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(enabled = !guardando, onClick = onCerrar) { Text("Cancelar") } }
    )
}
