package com.example.lunes26oct.domain.usecase

import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.repository.RepositorioGastos

class EditarCategoria(private val repositorio: RepositorioGastos) {
    operator fun invoke(categoria: Categoria): Boolean {
        if (categoria.nombre.isBlank()) return false
        return repositorio.actualizaCategoria(categoria.copy(nombre = categoria.nombre.trim()))
    }
}
