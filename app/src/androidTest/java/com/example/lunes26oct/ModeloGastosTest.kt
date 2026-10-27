package com.example.lunes26oct

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lunes26oct.data.local.ModeloGastos
import com.example.lunes26oct.data.repository.RepositorioGastosSqlite
import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ModeloGastosTest {
    private lateinit var context: Context
    private lateinit var modelo: ModeloGastos
    private val nombre = "prueba_gastos_${System.nanoTime()}.db"

    @Before fun preparar() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        modelo = ModeloGastos(context, nombre)
    }
    @After fun limpiar() { modelo.close(); context.deleteDatabase(nombre) }

    @Test fun matrizDelPdfConLasOnceFunciones() = runBlocking {
        var repo = RepositorioGastosSqlite(modelo)
        assertTrue(repo.insertaCategoria(Categoria(nombre = "Alimentacion")))
        assertTrue(repo.insertaCategoria(Categoria(nombre = "Transporte")))
        assertTrue(repo.insertaCategoria(Categoria(nombre = "Entretenimiento")))
        assertFalse(repo.insertaCategoria(Categoria(nombre = "  ")))
        assertFalse(repo.insertaCategoria(Categoria(nombre = "Alimentacion")))
        val categorias = repo.leeCategorias()
        val comida = categorias.first { it.nombre == "Alimentacion" }
        val transporte = categorias.first { it.nombre == "Transporte" }
        val ocio = categorias.first { it.nombre == "Entretenimiento" }
        assertEquals(comida, repo.leeCategoria(comida.id))
        assertTrue(repo.insertaGasto(Gasto(categoriaId = comida.id, descripcion = "Desayuno", monto = 85.50)))
        assertTrue(repo.insertaGasto(Gasto(categoriaId = comida.id, descripcion = "Comida", monto = 120.0)))
        assertTrue(repo.insertaGasto(Gasto(categoriaId = transporte.id, monto = 35.0)))
        fun comprobar(totales: Map<String, Double>, total: Double) {
            assertEquals(205.50, totales.getValue("Alimentacion"), 0.001)
            assertEquals(0.0, totales.getValue("Entretenimiento"), 0.0)
            assertEquals(total, totales.values.sum(), 0.001)
        }
        comprobar(repo.resumenCategorias().associate { it.nombre to it.total }, 240.50)
        val viaje = repo.leeGastos().first { it.categoriaId == transporte.id }
        assertNull(repo.leeGasto(viaje.id)!!.descripcion)
        assertTrue(repo.actualizaGasto(viaje.copy(monto = 40.0)))
        comprobar(repo.resumenCategorias().associate { it.nombre to it.total }, 245.50)
        for (monto in listOf(0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY, 0.004)) {
            assertFalse(repo.insertaGasto(viaje.copy(id = 0, monto = monto)))
            assertFalse(repo.actualizaGasto(viaje.copy(monto = monto)))
        }
        assertFalse(repo.insertaGasto(viaje.copy(id = 0, categoriaId = -1)))
        assertFalse(repo.actualizaGasto(viaje.copy(categoriaId = -1)))
        assertFalse(repo.eliminaCategoria(comida.id))
        assertEquals(3, repo.leeGastos().size)
        assertTrue(repo.actualizaCategoria(ocio.copy(nombre = "Ocio")))
        assertEquals("Ocio", repo.leeCategoria(ocio.id)!!.nombre)
        assertFalse(repo.actualizaCategoria(ocio.copy(nombre = "Alimentacion")))
        assertFalse(repo.actualizaCategoria(ocio.copy(nombre = "")))
        // Cerrar y crear otro helper verifica persistencia fuera de las listas de Compose.
        modelo.close()
        modelo = ModeloGastos(context, nombre)
        repo = RepositorioGastosSqlite(modelo)
        assertEquals(3, repo.leeGastos().size)
        assertEquals(40.0, repo.leeGasto(viaje.id)!!.monto, 0.0)
        assertTrue(repo.actualizaGasto(viaje.copy(categoriaId = ocio.id, descripcion = "", monto = 1.005)))
        assertEquals(1.01, repo.leeGasto(viaje.id)!!.monto, 0.0)
        assertEquals("", repo.leeGasto(viaje.id)!!.descripcion)
        assertTrue(repo.actualizaGasto(viaje.copy(monto = 40.0)))
        assertTrue(repo.eliminaGasto(viaje.id))
        assertNull(repo.leeGasto(viaje.id))
        assertTrue(repo.eliminaCategoria(transporte.id))
        assertNull(repo.leeCategoria(transporte.id))
        assertEquals(205.50, repo.resumenCategorias().sumOf { it.total }, 0.001)
    }

    @Test fun migracionConservaDatosIdsYSecuencia() {
        modelo.close()
        val archivo = context.getDatabasePath(nombre)
        archivo.parentFile!!.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(archivo, null).use { db ->
            db.execSQL("CREATE TABLE categorias (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL UNIQUE)")
            db.execSQL("CREATE TABLE gastos (id INTEGER PRIMARY KEY AUTOINCREMENT, categoria_id INTEGER NOT NULL, descripcion TEXT, monto INTEGER NOT NULL CHECK(monto > 0), FOREIGN KEY(categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT)")
            db.execSQL("INSERT INTO categorias(id,nombre) VALUES(7,'Transporte')")
            db.execSQL("INSERT INTO gastos(id,categoria_id,descripcion,monto) VALUES(12,7,NULL,85)")
            db.execSQL("INSERT INTO gastos(id,categoria_id,descripcion,monto) VALUES(99,7,'Eliminado',100)")
            db.execSQL("DELETE FROM gastos WHERE id=99")
            db.version = 1
        }
        modelo = ModeloGastos(context, nombre)
        assertEquals(85.0, modelo.leeGasto(12)!!.monto, 0.0)
        assertNull(modelo.leeGasto(12)!!.descripcion)
        assertEquals("Transporte", modelo.leeCategoria(7)!!.nombre)
        assertEquals(2, modelo.readableDatabase.version)
        assertFalse(modelo.eliminaCategoria(7))
        assertTrue(modelo.insertaGasto(Gasto(categoriaId = 7, monto = 85.50)))
        assertTrue(modelo.leeGastos().first().id > 99)
        modelo.readableDatabase.rawQuery("PRAGMA foreign_key_check", null).use { assertEquals(0, it.count) }
        modelo.readableDatabase.rawQuery("PRAGMA table_info(gastos)", null).use { cursor ->
            var tipo = ""
            while (cursor.moveToNext()) if (cursor.getString(1) == "monto") tipo = cursor.getString(2)
            assertEquals("DOUBLE", tipo)
        }
        modelo.close()
        modelo = ModeloGastos(context, nombre)
        assertEquals(170.50, modelo.resumenCategorias().single().total, 0.001)
    }
}
