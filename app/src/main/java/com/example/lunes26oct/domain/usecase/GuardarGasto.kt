package com.example.lunes26oct.domain.usecase

import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.domain.model.normalizaMonto
import com.example.lunes26oct.domain.repository.RepositorioGastos

class GuardarGasto(private val repositorio: RepositorioGastos) {
    suspend operator fun invoke(gasto: Gasto): Boolean {
        if (normalizaMonto(gasto.monto) == null) return false
        return if (gasto.id == 0) repositorio.insertaGasto(gasto)
            else repositorio.actualizaGasto(gasto)
    }
}
