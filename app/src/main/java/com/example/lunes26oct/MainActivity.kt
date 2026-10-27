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
import com.example.lunes26oct.domain.usecase.EditarCategoria
import com.example.lunes26oct.domain.usecase.EliminarCategoria
import com.example.lunes26oct.domain.usecase.CrearCategoria
import com.example.lunes26oct.ui.screens.categorias.Categorias
import com.example.lunes26oct.ui.screens.categorias.NuevaCategoria
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
        val crearCategoria = CrearCategoria(repositorio)
        val editarCategoria = EditarCategoria(repositorio)
        val eliminarCategoria = EliminarCategoria(repositorio)
        enableEdgeToEdge()
        setContent {
            var gastos by remember { mutableStateOf(repositorio.leeGastos()) }
            var mensaje by remember { mutableStateOf("") }
            var agregando by rememberSaveable { mutableStateOf(false) }
            var seccion by rememberSaveable { mutableStateOf("inicio") }
            var categoriaDetalleId by rememberSaveable { mutableStateOf<Int?>(null) }
            var creandoCategoria by rememberSaveable { mutableStateOf(false) }
            var editandoCategoriaId by rememberSaveable { mutableStateOf<Int?>(null) }
            var eliminandoCategoriaId by rememberSaveable { mutableStateOf<Int?>(null) }
            var errorEliminarCategoria by remember { mutableStateOf("") }
            val viendoLista = seccion == "gastos"
            val viendoCategorias = seccion == "categorias"
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
            var categorias by remember { mutableStateOf(repositorio.leeCategorias()) }
            BackHandler(enabled = agregando || seccion != "inicio") {
                if (agregando) {
                    agregando = false
                    editandoId = null
                } else if (categoriaDetalleId != null) categoriaDetalleId = null
                else seccion = "inicio"
            }

            Lunes26octTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        BarraInicio(
                            agregando = agregando,
                            viendoLista = viendoLista,
                            viendoCategorias = viendoCategorias,
                            agregandoCategoria = viendoCategorias,
                            onInicio = {
                                agregando = false
                                seccion = "inicio"
                                categoriaDetalleId = null
                                editandoId = null
                            },
                            onAgregar = {
                                if (viendoCategorias) {
                                    editandoCategoriaId = null
                                    creandoCategoria = true
                                }
                                else {
                                    editandoId = null
                                    agregando = true
                                }
                            },
                            onGastos = {
                                agregando = false
                                seccion = "gastos"
                                categoriaDetalleId = null
                                editandoId = null
                            },
                            onCategorias = {
                                seccion = "categorias"
                                categoriaDetalleId = null
                                agregando = false
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
                    } else if (viendoLista || categoriaDetalleId != null) {
                        ListaGastos(
                            gastos = if (categoriaDetalleId == null) gastos else gastos.filter { it.categoriaId == categoriaDetalleId },
                            categorias = categorias,
                            onEditar = editar,
                            onEliminar = eliminar,
                            onVolver = {
                                if (categoriaDetalleId != null) categoriaDetalleId = null
                                else seccion = "inicio"
                            },
                            titulo = categorias.firstOrNull { it.id == categoriaDetalleId }?.nombre ?: "Lista de gastos",
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else if (viendoCategorias) {
                        Categorias(
                            categorias = categorias,
                            gastos = gastos,
                            onVolver = { seccion = "inicio" },
                            onAgregar = { editandoCategoriaId = null; creandoCategoria = true },
                            onCategoria = { categoriaDetalleId = it.id },
                            onEditar = { categoria ->
                                editandoCategoriaId = categoria.id
                                creandoCategoria = true
                            },
                            onEliminar = { categoria ->
                                eliminandoCategoriaId = categoria.id
                                errorEliminarCategoria = ""
                            },
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
                if (creandoCategoria) {
                    NuevaCategoria(
                        categoriaInicial = categorias.firstOrNull { it.id == editandoCategoriaId },
                        onCerrar = { creandoCategoria = false; editandoCategoriaId = null },
                        onGuardar = { nombre ->
                            val actual = categorias.firstOrNull { it.id == editandoCategoriaId }
                            val guardada = if (actual == null) crearCategoria(nombre)
                                else editarCategoria(actual.copy(nombre = nombre))
                            if (guardada) {
                                categorias = repositorio.leeCategorias()
                                creandoCategoria = false
                                editandoCategoriaId = null
                            }
                            guardada
                        }
                    )
                }
                val categoriaAEliminar = categorias.firstOrNull { it.id == eliminandoCategoriaId }
                if (categoriaAEliminar != null) {
                    val tieneGastos = gastos.any { it.categoriaId == categoriaAEliminar.id }
                    AlertDialog(
                        onDismissRequest = { eliminandoCategoriaId = null },
                        title = { Text(if (tieneGastos) "Categoria con gastos" else "Eliminar categoria") },
                        text = {
                            Text(
                                if (tieneGastos) "No puedes eliminar ${categoriaAEliminar.nombre} porque tiene gastos asociados."
                                else errorEliminarCategoria.ifEmpty { "¿Eliminar ${categoriaAEliminar.nombre}? Esta accion no se puede deshacer." }
                            )
                        },
                        confirmButton = {
                            if (tieneGastos) {
                                TextButton(onClick = { eliminandoCategoriaId = null }) { Text("Entendido") }
                            } else {
                                TextButton(onClick = {
                                    if (eliminarCategoria(categoriaAEliminar.id)) {
                                        categorias = repositorio.leeCategorias()
                                        eliminandoCategoriaId = null
                                    } else errorEliminarCategoria = "No se pudo eliminar. Revisa si tiene gastos asociados."
                                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                            }
                        },
                        dismissButton = {
                            if (!tieneGastos) TextButton(onClick = { eliminandoCategoriaId = null }) { Text("Cancelar") }
                        }
                    )
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
