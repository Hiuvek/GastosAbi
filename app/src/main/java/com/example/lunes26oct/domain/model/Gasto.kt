package com.example.lunes26oct.domain.model
data class Gasto(
    val id: Int = 0,
    val categoriaId: Int,
    val descripcion: String? = null,
    val monto: Long
)
