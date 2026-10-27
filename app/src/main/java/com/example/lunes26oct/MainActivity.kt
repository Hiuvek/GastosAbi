package com.example.lunes26oct

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.lunes26oct.domain.model.Gasto
import androidx.compose.material3.MaterialTheme
import com.example.lunes26oct.ui.theme.Lunes26octTheme
import com.example.lunes26oct.data.local.ModeloGastos

import com.example.lunes26oct.data.repository.RepositorioGastosSqlite
import com.example.lunes26oct.domain.usecase.GuardarGasto
import com.example.lunes26oct.domain.usecase.EliminarGasto
import com.example.lunes26oct.ui.components.BarraInicio
import com.example.lunes26oct.ui.screens.inicio.PantallaPrincipal
import com.example.lunes26oct.ui.screens.gastos.AgregarGasto
import com.example.lunes26oct.ui.screens.gastos.ListaGastos

class MainActivity : ComponentActivity() {
    private lateinit var modelo: ModeloGastos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modelo = ModeloGastos(applicationContext)
        val repositorio = RepositorioGastosSqlite(modelo)
        val guardarGasto = GuardarGasto(repositorio)
        val eliminarGasto = EliminarGasto(repositorio)
        enableEdgeToEdge()
        setContent {
            var gastos by remember { mutableStateOf(repositorio.leeGastos()) }
            var mensaje by remember { mutableStateOf("") }
            var agregando by rememberSaveable { mutableStateOf(false) }
            var viendoLista by rememberSaveable { mutableStateOf(false) }
            var editandoId by rememberSaveable { mutableStateOf<Int?>(null) }
            var eliminandoId by rememberSaveable { mutableStateOf<Int?>(null) }
            var errorEliminar by remember { mutableStateOf("") }
            val editar: (Gasto) -> Unit = { gasto ->
                editandoId = gasto.id
                agregando = true
            }
            val eliminar: (Gasto) -> Unit = { gasto ->
                eliminandoId = gasto.id
                errorEliminar = ""
            }
            val categorias = remember { repositorio.leeCategorias() }
            BackHandler(enabled = agregando || viendoLista) {
                if (agregando) {
                    agregando = false
                    editandoId = null
                } else viendoLista = false
            }

            Lunes26octTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        BarraInicio(
                            agregando = agregando,
                            viendoLista = viendoLista,
                            onInicio = {
                                agregando = false
                                viendoLista = false
                                editandoId = null
                            },
                            onAgregar = { editandoId = null; agregando = true },
                            onGastos = {
                                agregando = false
                                viendoLista = true
                                editandoId = null
                            }
                        )
                    }
                ) { innerPadding ->
                    if (agregando) {
                        AgregarGasto(
                            categorias = categorias,
                            gastoInicial = gastos.firstOrNull { it.id == editandoId },
                            onGuardar = { monto, concepto, categoriaId ->
                                val gasto = Gasto(
                                    id = editandoId ?: 0,
                                    categoriaId = categoriaId,
                                    descripcion = concepto,
                                    monto = monto
                                )
                                val guardado = guardarGasto(gasto)
                                if (guardado) {
                                    gastos = repositorio.leeGastos()
                                    mensaje = if (editandoId == null) "Gasto guardado." else "Gasto actualizado."
                                    editandoId = null
                                    agregando = false
                                }
                                guardado
                            },
                            onVolver = { agregando = false; editandoId = null },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else if (viendoLista) {
                        ListaGastos(
                            gastos = gastos,
                            categorias = categorias,
                            onEditar = editar,
                            onEliminar = eliminar,
                            onVolver = { viendoLista = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        PantallaPrincipal(
                            gastos = gastos,
                            categorias = categorias,
                            onEditar = editar,
                            onEliminar = eliminar,
                            mensaje = mensaje,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
                val pendiente = gastos.firstOrNull { it.id == eliminandoId }
                if (pendiente != null) {
                    AlertDialog(
                        onDismissRequest = { eliminandoId = null },
                        title = { Text("Eliminar gasto") },
                        text = {
                            Text(
                                if (errorEliminar.isNotEmpty()) errorEliminar
                                else "¿Eliminar ${pendiente.descripcion?.takeIf { it.isNotBlank() } ?: "este gasto"}? Esta accion no se puede deshacer."
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (eliminarGasto(pendiente.id)) {
                                    gastos = repositorio.leeGastos()
                                    mensaje = "Gasto eliminado."
                                    eliminandoId = null
                                } else errorEliminar = "No se pudo eliminar el gasto. Intentalo de nuevo."
                            }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                        },
                        dismissButton = {
                            TextButton(onClick = { eliminandoId = null }) { Text("Cancelar") }
                        }
                    )
                }

            }
        }
    }

    override fun onDestroy() {
        modelo.close()
        super.onDestroy()
    }
}
