package com.example.lunes26oct

import android.database.sqlite.SQLiteException
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.lunes26oct.data.local.ModeloGastos
import com.example.lunes26oct.data.local.PreferenciasIconosCategoria
import com.example.lunes26oct.data.repository.RepositorioGastosSqlite
import com.example.lunes26oct.domain.model.*
import com.example.lunes26oct.domain.usecase.*
import com.example.lunes26oct.ui.components.BarraInicio
import com.example.lunes26oct.ui.components.LocalIconosCategoria
import com.example.lunes26oct.ui.screens.categorias.Categorias
import com.example.lunes26oct.ui.screens.categorias.NuevaCategoria
import com.example.lunes26oct.ui.screens.gastos.AgregarGasto
import com.example.lunes26oct.ui.screens.gastos.ListaGastos
import com.example.lunes26oct.ui.screens.inicio.PantallaPrincipal
import com.example.lunes26oct.ui.theme.Lunes26octTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var modelo: ModeloGastos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modelo = ModeloGastos(applicationContext)
        val repositorio = RepositorioGastosSqlite(modelo)
        val preferencias = PreferenciasIconosCategoria(applicationContext)
        val guardarGasto = GuardarGasto(repositorio)
        val eliminarGasto = EliminarGasto(repositorio)
        val crearCategoria = CrearCategoria(repositorio)
        val editarCategoria = EditarCategoria(repositorio)
        val eliminarCategoria = EliminarCategoria(repositorio)
        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            var gastos by remember { mutableStateOf(emptyList<Gasto>()) }
            var categorias by remember { mutableStateOf(emptyList<Categoria>()) }
            var resumen by remember { mutableStateOf(emptyList<ResumenCategoria>()) }
            var iconos by remember { mutableStateOf(preferencias.leer()) }
            var cargando by remember { mutableStateOf(true) }
            var ocupado by remember { mutableStateOf(false) }
            var error by remember { mutableStateOf("") }
            var mensaje by remember { mutableStateOf("") }
            var seccion by rememberSaveable { mutableStateOf("inicio") }
            var agregando by rememberSaveable { mutableStateOf(false) }
            var creandoCategoria by rememberSaveable { mutableStateOf(false) }
            var categoriaDetalleId by rememberSaveable { mutableStateOf<Int?>(null) }
            var editandoId by rememberSaveable { mutableStateOf<Int?>(null) }
            var editandoCategoriaId by rememberSaveable { mutableStateOf<Int?>(null) }
            var eliminandoId by rememberSaveable { mutableStateOf<Int?>(null) }
            var eliminandoCategoriaId by rememberSaveable { mutableStateOf<Int?>(null) }
            var gastoInicial by remember { mutableStateOf<Gasto?>(null) }
            var categoriaInicial by remember { mutableStateOf<Categoria?>(null) }

            suspend fun recargar() {
                // El repositorio consulta en IO; el estado se publica junto en el hilo principal.
                val nuevasCategorias = repositorio.leeCategorias()
                val nuevosGastos = repositorio.leeGastos()
                val nuevoResumen = repositorio.resumenCategorias()
                categorias = nuevasCategorias
                gastos = nuevosGastos
                resumen = nuevoResumen
            }
            suspend fun operar(accion: suspend () -> Boolean): Boolean {
                if (ocupado) return false
                ocupado = true
                return try {
                    accion()
                } catch (_: SQLiteException) {
                    error = "No se pudo acceder a los datos. Intenta de nuevo."
                    false
                } finally { ocupado = false }
            }
            LaunchedEffect(Unit) {
                operar {
                    recargar()
                    gastoInicial = editandoId?.let { repositorio.leeGasto(it) }
                    categoriaInicial = editandoCategoriaId?.let { repositorio.leeCategoria(it) }
                    if (editandoId != null && gastoInicial == null) { editandoId = null; agregando = false }
                    if (editandoCategoriaId != null && categoriaInicial == null) { editandoCategoriaId = null; creandoCategoria = false }
                    true
                }
                cargando = false
            }
            fun abrirCategoria() {
                if (ocupado || cargando) return
                seccion = "categorias"
                agregando = false
                editandoId = null
                gastoInicial = null
                categoriaDetalleId = null
                editandoCategoriaId = null
                categoriaInicial = null
                creandoCategoria = true
            }
            fun abrirGasto() {
                if (ocupado || cargando) return
                editandoId = null
                gastoInicial = null
                agregando = true
            }
            fun volver() {
                if (ocupado) return
                if (agregando) { agregando = false; editandoId = null; gastoInicial = null }
                else if (categoriaDetalleId != null) categoriaDetalleId = null
                else seccion = "inicio"
            }
            fun navegar(destino: String) {
                if (ocupado || cargando) return
                seccion = destino
                agregando = false
                editandoId = null
                gastoInicial = null
                categoriaDetalleId = null
            }
            val editar: (Gasto) -> Unit = { gasto ->
                scope.launch {
                    operar {
                        val actual = repositorio.leeGasto(gasto.id)
                        if (actual != null) {
                            gastoInicial = actual
                            editandoId = actual.id
                            agregando = true
                        } else error = "El gasto ya no existe."
                        actual != null
                    }
                }
            }
            val eliminar: (Gasto) -> Unit = { if (!ocupado) eliminandoId = it.id }
            BackHandler(enabled = ocupado || agregando || seccion != "inicio") { volver() }

            Lunes26octTheme {
                CompositionLocalProvider(LocalIconosCategoria provides iconos) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            BarraInicio(
                                agregando = agregando,
                                viendoLista = seccion == "gastos",
                                viendoCategorias = seccion == "categorias",
                                agregandoCategoria = seccion == "categorias" && !agregando,
                                onInicio = { navegar("inicio") },
                                onGastos = { navegar("gastos") },
                                onCategorias = { navegar("categorias") },
                                onAgregar = { if (seccion == "categorias" && !agregando) abrirCategoria() else abrirGasto() }
                            )
                        }
                    ) { padding ->
                        val contenido = Modifier.padding(padding)
                        when {
                            cargando -> Box(contenido.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                            agregando -> AgregarGasto(
                                categorias = categorias,
                                gastoInicial = gastoInicial,
                                onCrearCategoria = { abrirCategoria() },
                                onVolver = { volver() },
                                onGuardar = { monto, concepto, categoriaId ->
                                    operar {
                                        val guardado = guardarGasto(Gasto(editandoId ?: 0, categoriaId, concepto.takeIf { it.isNotBlank() }, monto))
                                        if (guardado) {
                                            recargar()
                                            mensaje = if (editandoId == null) "Gasto guardado." else "Gasto actualizado."
                                            editandoId = null
                                            gastoInicial = null
                                            agregando = false
                                        }
                                        guardado
                                    }
                                },
                                modifier = contenido
                            )
                            seccion == "gastos" || categoriaDetalleId != null -> ListaGastos(
                                gastos = gastos.filter { categoriaDetalleId == null || it.categoriaId == categoriaDetalleId },
                                categorias = categorias,
                                onEditar = editar,
                                onEliminar = eliminar,
                                onVolver = { volver() },
                                titulo = categorias.firstOrNull { it.id == categoriaDetalleId }?.nombre ?: "Lista de gastos",
                                modifier = contenido
                            )
                            seccion == "categorias" -> Categorias(
                                categorias = categorias, gastos = gastos, resumen = resumen,
                                onVolver = { volver() },
                                onAgregar = { abrirCategoria() },
                                onCategoria = { if (!ocupado) categoriaDetalleId = it.id },
                                onEditar = { categoria ->
                                    scope.launch {
                                        operar {
                                            val actual = repositorio.leeCategoria(categoria.id)
                                            if (actual != null) {
                                                categoriaInicial = actual
                                                editandoCategoriaId = actual.id
                                                creandoCategoria = true
                                            } else error = "La categoria ya no existe."
                                            actual != null
                                        }
                                    }
                                },
                                onEliminar = { if (!ocupado) eliminandoCategoriaId = it.id },
                                modifier = contenido
                            )
                            else -> PantallaPrincipal(gastos, resumen, categorias, editar, eliminar, mensaje, contenido)
                        }
                    }
                    if (creandoCategoria && !cargando) {
                        NuevaCategoria(
                            categoriaInicial = categoriaInicial,
                            onCerrar = { if (!ocupado) { creandoCategoria = false; editandoCategoriaId = null; categoriaInicial = null } },
                            onGuardar = { nombre, icono ->
                                operar {
                                    val actual = categoriaInicial
                                    val guardada = if (actual == null) crearCategoria(nombre)
                                        else actual.nombre == nombre || editarCategoria(actual.copy(nombre = nombre))
                                    if (guardada) {
                                        recargar()
                                        val id = actual?.id ?: categorias.firstOrNull { it.nombre == nombre }?.id
                                        if (id != null) preferencias.guardar(id, icono)
                                        iconos = preferencias.leer()
                                        creandoCategoria = false
                                        editandoCategoriaId = null
                                        categoriaInicial = null
                                        if (actual == null) {
                                            seccion = "gastos"
                                            categoriaDetalleId = null
                                            editandoId = null
                                            gastoInicial = null
                                            agregando = true
                                        }
                                    }
                                    guardada
                                }
                            }
                        )
                    }
                    categorias.firstOrNull { it.id == eliminandoCategoriaId }?.let { categoria ->
                        val tieneGastos = gastos.any { it.categoriaId == categoria.id }
                        AlertDialog(
                            onDismissRequest = { if (!ocupado) eliminandoCategoriaId = null },
                            title = { Text(if (tieneGastos) "Categoria con gastos" else "Eliminar categoria") },
                            text = { Text(if (tieneGastos) "No puedes eliminar ${categoria.nombre} porque tiene gastos asociados." else "¿Eliminar ${categoria.nombre}?") },
                            confirmButton = {
                                TextButton(enabled = !ocupado, onClick = {
                                    if (tieneGastos) eliminandoCategoriaId = null
                                    else scope.launch {
                                        operar {
                                            if (eliminarCategoria(categoria.id)) {
                                                preferencias.eliminar(categoria.id)
                                                iconos = preferencias.leer()
                                                recargar()
                                                eliminandoCategoriaId = null
                                                true
                                            } else {
                                                error = "No se pudo eliminar. Revisa si tiene gastos asociados."
                                                false
                                            }
                                        }
                                    }
                                }) { Text(if (tieneGastos) "Entendido" else "Eliminar") }
                            },
                            dismissButton = { if (!tieneGastos) TextButton(enabled = !ocupado, onClick = { eliminandoCategoriaId = null }) { Text("Cancelar") } }
                        )
                    }
                    gastos.firstOrNull { it.id == eliminandoId }?.let { gasto ->
                        AlertDialog(
                            onDismissRequest = { if (!ocupado) eliminandoId = null },
                            title = { Text("Eliminar gasto") },
                            text = { Text("¿Eliminar ${gasto.descripcion?.takeIf { it.isNotBlank() } ?: "este gasto"}? Esta accion no se puede deshacer.") },
                            confirmButton = {
                                TextButton(enabled = !ocupado, onClick = {
                                    scope.launch {
                                        operar {
                                            if (eliminarGasto(gasto.id)) {
                                                recargar()
                                                eliminandoId = null
                                                mensaje = "Gasto eliminado."
                                                true
                                            } else { error = "No se pudo eliminar el gasto."; false }
                                        }
                                    }
                                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                            },
                            dismissButton = { TextButton(enabled = !ocupado, onClick = { eliminandoId = null }) { Text("Cancelar") } }
                        )
                    }
                    if (error.isNotEmpty()) AlertDialog(
                        onDismissRequest = { error = "" },
                        title = { Text("No se pudo completar la operacion") },
                        text = { Text(error) },
                        confirmButton = { TextButton(onClick = { error = ""; scope.launch { operar { recargar(); true } } }) { Text("Reintentar") } },
                        dismissButton = { TextButton(onClick = { error = "" }) { Text("Cerrar") } }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        modelo.close()
    }
}
