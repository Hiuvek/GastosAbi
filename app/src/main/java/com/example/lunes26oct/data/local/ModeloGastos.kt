package com.example.lunes26oct.data.local
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.lunes26oct.domain.model.Categoria
import com.example.lunes26oct.domain.model.Gasto
import com.example.lunes26oct.domain.model.ResumenCategoria
import com.example.lunes26oct.domain.model.normalizaMonto

class ModeloGastos(context: Context, nombreBaseDatos: String = "gastos.db") :
    SQLiteOpenHelper(context, nombreBaseDatos, null, 2) {

    private companion object {
        const val TABLA_CATEGORIAS = "categorias"
        const val TABLA_GASTOS = "gastos"
        const val COLUMNA_ID = "id"
        const val COLUMNA_NOMBRE = "nombre"
        const val COLUMNA_CATEGORIA_ID = "categoria_id"
        const val COLUMNA_DESCRIPCION = "descripcion"
        const val COLUMNA_MONTO = "monto"
    }

    // Activa la integridad referencial.
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE categorias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL UNIQUE
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE gastos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                categoria_id INTEGER NOT NULL,
                descripcion TEXT,
                monto DOUBLE NOT NULL CHECK (monto > 0),
                FOREIGN KEY (categoria_id)
                    REFERENCES categorias(id)
                    ON DELETE RESTRICT
            )
        """.trimIndent())
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 2) {
            // SQLiteOpenHelper ejecuta onUpgrade dentro de una transaccion.
            // Mantener el valor mostrado por la version 1: 85 pasa a 85.00.
            val secuencia = db.rawQuery(
                "SELECT seq FROM sqlite_sequence WHERE name = 'gastos'", null
            ).use { if (it.moveToFirst()) it.getLong(0) else 0L }
            db.execSQL("""
                CREATE TABLE gastos_nueva (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    categoria_id INTEGER NOT NULL,
                    descripcion TEXT,
                    monto DOUBLE NOT NULL CHECK (monto > 0),
                    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
                )
            """.trimIndent())
            db.execSQL("""
                INSERT INTO gastos_nueva (id, categoria_id, descripcion, monto)
                SELECT id, categoria_id, descripcion, CAST(monto AS DOUBLE) FROM gastos
            """.trimIndent())
            db.execSQL("DROP TABLE gastos")
            db.execSQL("ALTER TABLE gastos_nueva RENAME TO gastos")
            db.execSQL("UPDATE sqlite_sequence SET seq = MAX(seq, ?) WHERE name = 'gastos'", arrayOf(secuencia))
        }
    }

    // Crea los valores usados para guardar una categoria.
    private fun valoresCategoria(categoria: Categoria) = ContentValues().apply {
        put(COLUMNA_NOMBRE, categoria.nombre.trim())
    }

    // Crea los valores usados para guardar un gasto.
    private fun valoresGasto(gasto: Gasto, monto: Double) = ContentValues().apply {
        put(COLUMNA_CATEGORIA_ID, gasto.categoriaId)
        put(COLUMNA_DESCRIPCION, gasto.descripcion)
        put(COLUMNA_MONTO, monto)
    }

    // Unifica el manejo de errores de escritura SQLite.
    private fun ejecutarEscritura(operacion: () -> Boolean): Boolean = try {
        operacion()
    } catch (_: SQLiteException) {
        false
    }

    // Convierte la fila actual del cursor en una categoria.
    private fun cursorCategoria(c: Cursor): Categoria {
        return Categoria(
            id = c.getInt(c.getColumnIndexOrThrow("id")),
            nombre = c.getString(
                c.getColumnIndexOrThrow("nombre")
            )
        )
    }

    // Convierte la fila actual del cursor en un gasto.
    private fun cursorGasto(c: Cursor): Gasto {
        val indiceDescripcion =
            c.getColumnIndexOrThrow("descripcion")

        return Gasto(
            id = c.getInt(c.getColumnIndexOrThrow("id")),
            categoriaId = c.getInt(
                c.getColumnIndexOrThrow("categoria_id")
            ),
            descripcion = if (c.isNull(indiceDescripcion))
                null
            else
                c.getString(indiceDescripcion),
            monto = c.getDouble(
                c.getColumnIndexOrThrow("monto")
            )
        )
    }

    // =========================================
    //              CATEGORIAS
    // =========================================

    fun leeCategoria(id: Int): Categoria? {
        val db = readableDatabase

        db.query(
            "categorias",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst())
                cursorCategoria(cursor)
            else
                null
        }
    }

    fun leeCategorias(): List<Categoria> {
        val categorias = mutableListOf<Categoria>()
        val db = readableDatabase

        db.query(
            "categorias",
            null,
            null, null, null, null,
            "nombre ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                categorias.add(cursorCategoria(cursor))
            }
        }

        return categorias
    }

    fun insertaCategoria(categoria: Categoria): Boolean {
        if (categoria.nombre.isBlank()) return false

        return ejecutarEscritura {
            writableDatabase.insert(
                TABLA_CATEGORIAS, null, valoresCategoria(categoria)
            ) != -1L
        }
    }

    fun actualizaCategoria(categoria: Categoria): Boolean {
        if (categoria.nombre.isBlank()) return false

        return ejecutarEscritura {
            writableDatabase.update(
                TABLA_CATEGORIAS,
                valoresCategoria(categoria),
                "$COLUMNA_ID = ?",
                arrayOf(categoria.id.toString())
            ) > 0
        }
    }

    fun eliminaCategoria(id: Int): Boolean {
        return ejecutarEscritura {
            writableDatabase.delete(
                TABLA_CATEGORIAS,
                "$COLUMNA_ID = ?",
                arrayOf(id.toString())
            ) > 0
        }
    }

    // =========================================
    //                 GASTOS
    // =========================================

    fun leeGasto(id: Int): Gasto? {
        val db = readableDatabase

        db.query(
            "gastos",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        ).use { cursor ->
            return if (cursor.moveToFirst())
                cursorGasto(cursor)
            else
                null
        }
    }

    fun leeGastos(): List<Gasto> {
        val gastos = mutableListOf<Gasto>()
        val db = readableDatabase

        db.query(
            "gastos",
            null,
            null, null, null, null,
            "id DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                gastos.add(cursorGasto(cursor))
            }
        }

        return gastos
    }

    fun insertaGasto(gasto: Gasto): Boolean {
        val monto = normalizaMonto(gasto.monto) ?: return false

        return ejecutarEscritura {
            writableDatabase.insert(
                TABLA_GASTOS, null, valoresGasto(gasto, monto)
            ) != -1L
        }
    }

    fun actualizaGasto(gasto: Gasto): Boolean {
        val monto = normalizaMonto(gasto.monto) ?: return false

        return ejecutarEscritura {
            writableDatabase.update(
                TABLA_GASTOS,
                valoresGasto(gasto, monto),
                "$COLUMNA_ID = ?",
                arrayOf(gasto.id.toString())
            ) > 0
        }
    }

    fun eliminaGasto(id: Int): Boolean {
        return ejecutarEscritura {
            writableDatabase.delete(
                TABLA_GASTOS,
                "$COLUMNA_ID = ?",
                arrayOf(id.toString())
            ) > 0
        }
    }

    fun resumenCategorias(): List<ResumenCategoria> {
        val resumen = mutableListOf<ResumenCategoria>()

        val sql = """
			SELECT c.nombre,
				   ROUND(COALESCE(SUM(g.monto), 0), 2) AS total
			FROM categorias c
			LEFT JOIN gastos g
				ON c.id = g.categoria_id
			GROUP BY c.id, c.nombre
			ORDER BY c.nombre
		""".trimIndent()

        readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                resumen.add(
                    ResumenCategoria(
                        nombre = cursor.getString(0),
                        total = cursor.getDouble(1)
                    )
                )
            }
        }

        return resumen
    }
}
