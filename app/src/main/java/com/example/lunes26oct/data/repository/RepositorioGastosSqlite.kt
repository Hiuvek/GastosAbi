package com.example.lunes26oct.data.repository

import com.example.lunes26oct.data.local.ModeloGastos
import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.domain.repository.RepositorioGastos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositorioGastosSqlite(private val modelo: ModeloGastos) : RepositorioGastos {
    // Ejecuta las operaciones SQLite fuera del hilo principal.
    private suspend fun <T> enIo(accion: () -> T): T =
        withContext(Dispatchers.IO) { accion() }

    override suspend fun insertaCategoria(categoria: Categoria) = enIo { modelo.insertaCategoria(categoria) }
    override suspend fun actualizaCategoria(categoria: Categoria) = enIo { modelo.actualizaCategoria(categoria) }
    override suspend fun eliminaCategoria(id: Int) = enIo { modelo.eliminaCategoria(id) }
    override suspend fun leeCategorias() = enIo { modelo.leeCategorias() }
    override suspend fun leeGastos() = enIo { modelo.leeGastos() }
    override suspend fun insertaGasto(gasto: Gasto) = enIo { modelo.insertaGasto(gasto) }
    override suspend fun actualizaGasto(gasto: Gasto) = enIo { modelo.actualizaGasto(gasto) }
    override suspend fun eliminaGasto(id: Int) = enIo { modelo.eliminaGasto(id) }
    override suspend fun leeCategoria(id: Int) = enIo { modelo.leeCategoria(id) }
    override suspend fun leeGasto(id: Int) = enIo { modelo.leeGasto(id) }
    override suspend fun resumenCategorias() = enIo { modelo.resumenCategorias() }
}
