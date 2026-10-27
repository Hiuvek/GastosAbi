package com.example.lunes26oct.data.repository

import com.example.lunes26oct.data.local.ModeloGastos
import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.domain.model.ResumenCategoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.lunes26oct.domain.repository.RepositorioGastos

class RepositorioGastosSqlite(private val modelo: ModeloGastos) : RepositorioGastos {
    override suspend fun insertaCategoria(categoria: Categoria): Boolean = withContext(Dispatchers.IO) { modelo.insertaCategoria(categoria) }
    override suspend fun actualizaCategoria(categoria: Categoria): Boolean = withContext(Dispatchers.IO) { modelo.actualizaCategoria(categoria) }
    override suspend fun eliminaCategoria(id: Int): Boolean = withContext(Dispatchers.IO) { modelo.eliminaCategoria(id) }
    override suspend fun leeCategorias(): List<Categoria> = withContext(Dispatchers.IO) { modelo.leeCategorias() }
    override suspend fun leeGastos(): List<Gasto> = withContext(Dispatchers.IO) { modelo.leeGastos() }
    override suspend fun insertaGasto(gasto: Gasto): Boolean = withContext(Dispatchers.IO) { modelo.insertaGasto(gasto) }
    override suspend fun actualizaGasto(gasto: Gasto): Boolean = withContext(Dispatchers.IO) { modelo.actualizaGasto(gasto) }
    override suspend fun eliminaGasto(id: Int): Boolean = withContext(Dispatchers.IO) { modelo.eliminaGasto(id) }
    override suspend fun leeCategoria(id: Int): Categoria? = withContext(Dispatchers.IO) { modelo.leeCategoria(id) }
    override suspend fun leeGasto(id: Int): Gasto? = withContext(Dispatchers.IO) { modelo.leeGasto(id) }
    override suspend fun resumenCategorias(): List<ResumenCategoria> = withContext(Dispatchers.IO) { modelo.resumenCategorias() }
}
