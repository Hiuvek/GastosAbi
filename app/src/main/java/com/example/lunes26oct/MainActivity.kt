package com.example.lunes26oct

import android.os.Bundle
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            var agregando by rememberSaveable { mutableStateOf(false) }
            val categorias = remember { modelo.leeCategorias() }
            BackHandler(enabled = agregando) { agregando = false }

            Lunes26octTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (agregando) {
                        AgregarGasto(
                            categorias = categorias,
                            onGuardar = { monto, concepto, categoriaId ->
                                val guardado = modelo.insertaGasto(
                                    Gasto(categoriaId = categoriaId, descripcion = concepto, monto = monto)
                                )
                                if (guardado) {
                                    resumen = modelo.resumenCategorias()
                                    mensaje = "Gasto guardado."
                                    agregando = false
                                }
                                guardado
                            },
                            onVolver = { agregando = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Gastos por categoría", style = MaterialTheme.typography.headlineSmall)
                        Button(onClick = { agregando = true }) {
                            Text("Agregar gasto")
                        }
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

    override fun onDestroy() {
        modelo.close()
        super.onDestroy()
    }
}
