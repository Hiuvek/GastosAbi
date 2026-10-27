package com.example.lunes26oct.data.local

import android.content.Context

// La eleccion visual se guarda aparte de gastos.db, asociada al ID de categoria.
class PreferenciasIconosCategoria(context: Context) {
    private companion object {
        const val ARCHIVO_PREFERENCIAS = "iconos_categoria"
        const val PREFIJO_CATEGORIA = "categoria_"
    }

    private val preferencias = context.applicationContext.getSharedPreferences(
        ARCHIVO_PREFERENCIAS,
        Context.MODE_PRIVATE
    )

    private fun claveCategoria(categoriaId: Int) = "$PREFIJO_CATEGORIA$categoriaId"

    private fun idCategoria(clave: String): Int? =
        clave.takeIf { it.startsWith(PREFIJO_CATEGORIA) }
            ?.removePrefix(PREFIJO_CATEGORIA)
            ?.toIntOrNull()

    fun leer(): Map<Int, String> = preferencias.all.mapNotNull { (clave, valor) ->
        val id = idCategoria(clave)
        if (id != null && valor is String) id to valor else null
    }.toMap()

    fun guardar(categoriaId: Int, icono: String) {
        preferencias.edit().putString(claveCategoria(categoriaId), icono).apply()
    }

    fun eliminar(categoriaId: Int) {
        preferencias.edit().remove(claveCategoria(categoriaId)).apply()
    }
}
