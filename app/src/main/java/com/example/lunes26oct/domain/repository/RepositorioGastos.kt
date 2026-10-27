package com.example.lunes26oct.domain.repository

import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto

interface RepositorioGastos {
    fun insertaCategoria(categoria: Categoria): Boolean
    fun leeCategorias(): List<Categoria>
    fun leeGastos(): List<Gasto>
    fun insertaGasto(gasto: Gasto): Boolean
    fun actualizaGasto(gasto: Gasto): Boolean
    fun eliminaGasto(id: Int): Boolean
}
