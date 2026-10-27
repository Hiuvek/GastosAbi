package com.example.lunes26oct

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.lunes26oct.ui.theme.Gasto
import androidx.compose.material3.MaterialTheme
import com.example.lunes26oct.ui.theme.Lunes26octTheme
import com.example.lunes26oct.ui.theme.ModeloGastos

class MainActivity : ComponentActivity() {
    private lateinit var modelo: ModeloGastos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modelo = ModeloGastos(applicationContext)
        enableEdgeToEdge()
        setContent {
            var gastos by remember { mutableStateOf(modelo.leeGastos()) }
            var mensaje by remember { mutableStateOf("") }
            var agregando by rememberSaveable { mutableStateOf(false) }
            var viendoLista by rememberSaveable { mutableStateOf(false) }
            val categorias = remember { modelo.leeCategorias() }
            BackHandler(enabled = agregando || viendoLista) {
                if (agregando) agregando = false else viendoLista = false
            }

            Lunes26octTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        BarraInicio(
                            agregando = agregando,
                            onInicio = {
                                agregando = false
                                viendoLista = false
                            },
                            onAgregar = { agregando = true }
                        )
                    }
                ) { innerPadding ->
                    if (agregando) {
                        AgregarGasto(
                            categorias = categorias,
                            onGuardar = { monto, concepto, categoriaId ->
                                val guardado = modelo.insertaGasto(
                                    Gasto(categoriaId = categoriaId, descripcion = concepto, monto = monto)
                                )
                                if (guardado) {
                                    gastos = modelo.leeGastos()
                                    mensaje = "Gasto guardado."
                                    agregando = false
                                }
                                guardado
                            },
                            onVolver = { agregando = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else if (viendoLista) {
                        ListaGastos(
                            gastos = gastos,
                            categorias = categorias,
                            onVolver = { viendoLista = false },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        PantallaPrincipal(
                            gastos = gastos,
                            categorias = categorias,
                            mensaje = mensaje,
                            onVerGastos = { viendoLista = true },
                            modifier = Modifier.padding(innerPadding)
                        )
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
