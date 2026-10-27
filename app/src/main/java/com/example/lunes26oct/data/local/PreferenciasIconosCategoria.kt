package com.example.lunes26oct.data.local

import android.content.Context

// La eleccion visual se guarda aparte de gastos.db, asociada al ID de categoria.
class PreferenciasIconosCategoria(context: Context) {
    private val preferencias = context.applicationContext.getSharedPreferences("iconos_categoria", Context.MODE_PRIVATE)

    fun leer(): Map<Int, String> = preferencias.all.mapNotNull { (clave, valor) ->
        val id = clave.removePrefix("categoria_").toIntOrNull()
        if (clave.startsWith("categoria_") && id != null && valor is String) id to valor else null
    }.toMap()

    fun guardar(categoriaId: Int, icono: String) {
        preferencias.edit().putString("categoria_$categoriaId", icono).apply()
    }

    fun eliminar(categoriaId: Int) {
        preferencias.edit().remove("categoria_$categoriaId").apply()
    }
}
