package com.example.lunes26oct.data.repository

import com.example.lunes26oct.data.local.ModeloGastos
import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.domain.repository.RepositorioGastos

class RepositorioGastosSqlite(private val modelo: ModeloGastos) : RepositorioGastos {
    override fun insertaCategoria(categoria: Categoria): Boolean = modelo.insertaCategoria(categoria)
    override fun actualizaCategoria(categoria: Categoria): Boolean = modelo.actualizaCategoria(categoria)
    override fun eliminaCategoria(id: Int): Boolean = modelo.eliminaCategoria(id)
    override fun leeCategorias(): List<Categoria> = modelo.leeCategorias()
    override fun leeGastos(): List<Gasto> = modelo.leeGastos()
    override fun insertaGasto(gasto: Gasto): Boolean = modelo.insertaGasto(gasto)
    override fun actualizaGasto(gasto: Gasto): Boolean = modelo.actualizaGasto(gasto)
    override fun eliminaGasto(id: Int): Boolean = modelo.eliminaGasto(id)
}
