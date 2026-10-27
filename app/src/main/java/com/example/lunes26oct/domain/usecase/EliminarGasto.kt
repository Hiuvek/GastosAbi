package com.example.lunes26oct.domain.usecase

import com.example.lunes26oct.domain.repository.RepositorioGastos

class EliminarGasto(private val repositorio: RepositorioGastos) {
    operator fun invoke(id: Int): Boolean = repositorio.eliminaGasto(id)
}
