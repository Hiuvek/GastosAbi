package com.example.lunes26oct.domain.usecase

import com.example.lunes26oct.domain.repository.RepositorioGastos

class EliminarCategoria(private val repositorio: RepositorioGastos) {
    suspend operator fun invoke(id: Int): Boolean = repositorio.eliminaCategoria(id)
}
