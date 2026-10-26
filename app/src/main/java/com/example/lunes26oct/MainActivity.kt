package com.example.lunes26oct

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lunes26oct.ui.theme.Categoria
import com.example.lunes26oct.ui.theme.Gasto
import com.example.lunes26oct.ui.theme.Lunes26octTheme
import com.example.lunes26oct.ui.theme.ModeloGastos

class MainActivity : ComponentActivity() {
    private lateinit var modelo: ModeloGastos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modelo = ModeloGastos(applicationContext)
        enableEdgeToEdge()
        setContent {
            var resumen by remember { mutableStateOf(modelo.resumenCategorias()) }
            var mensaje by remember { mutableStateOf("") }

            Lunes26octTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Gastos por categoría", style = MaterialTheme.typography.headlineSmall)
                        Button(onClick = {
                            mensaje = agregarGastoEjemplo()
                            resumen = modelo.resumenCategorias()
                        }) {
                            Text("Agregar gasto de ejemplo")
                        }
                        Text("Cada pulsación agrega un gasto de 8500 a Alimentación.")
                        if (mensaje.isNotEmpty()) Text(mensaje)
                        if (resumen.isEmpty()) Text("Todavía no hay categorías.")
                        resumen.forEach { categoria ->
                            Text("${categoria.nombre}: ${categoria.total}")
                        }
                    }
                }
            }
        }
    }

    private fun agregarGastoEjemplo(): String {
        // Crear la categoría solo si aún no existe.
        var categoria = modelo.leeCategorias().firstOrNull { it.nombre == "Alimentación" }
        if (categoria == null) {
            if (!modelo.insertaCategoria(Categoria(nombre = "Alimentación"))) {
                return "No se pudo crear la categoría."
            }
            categoria = modelo.leeCategorias().firstOrNull { it.nombre == "Alimentación" }
        }
        val categoriaId = categoria?.id ?: return "No se encontró la categoría."
        val guardado = modelo.insertaGasto(
            Gasto(
                categoriaId = categoriaId,
                descripcion = "Comida corrida",
                monto = 8500
            )
        )
        return if (guardado) "Gasto guardado." else "No se pudo guardar el gasto."
    }

    override fun onDestroy() {
        modelo.close()
        super.onDestroy()
    }
}
