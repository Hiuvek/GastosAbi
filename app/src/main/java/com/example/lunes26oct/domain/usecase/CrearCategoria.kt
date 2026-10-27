package com.example.lunes26oct.domain.usecase

import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.repository.RepositorioGastos

class CrearCategoria(private val repositorio: RepositorioGastos) {
    suspend operator fun invoke(nombre: String): Boolean {
        if (nombre.isBlank()) return false
        return repositorio.insertaCategoria(Categoria(nombre = nombre.trim()))
    }
}
