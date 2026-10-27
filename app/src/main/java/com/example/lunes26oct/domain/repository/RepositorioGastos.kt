package com.example.lunes26oct.domain.repository

import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.domain.model.ResumenCategoria

interface RepositorioGastos {
    suspend fun leeCategoria(id: Int): Categoria?
    suspend fun leeGasto(id: Int): Gasto?
    suspend fun resumenCategorias(): List<ResumenCategoria>
    suspend fun insertaCategoria(categoria: Categoria): Boolean
    suspend fun actualizaCategoria(categoria: Categoria): Boolean
    suspend fun eliminaCategoria(id: Int): Boolean
    suspend fun leeCategorias(): List<Categoria>
    suspend fun leeGastos(): List<Gasto>
    suspend fun insertaGasto(gasto: Gasto): Boolean
    suspend fun actualizaGasto(gasto: Gasto): Boolean
    suspend fun eliminaGasto(id: Int): Boolean
}
