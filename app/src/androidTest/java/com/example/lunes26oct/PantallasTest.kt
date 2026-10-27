package com.example.lunes26oct

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lunes26oct.domain.model.*
import com.example.lunes26oct.ui.screens.gastos.AgregarGasto
import com.example.lunes26oct.ui.screens.categorias.NuevaCategoria
import com.example.lunes26oct.ui.screens.inicio.PantallaPrincipal
import com.example.lunes26oct.ui.theme.Lunes26octTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class PantallasTest {
    @get:Rule val compose = createComposeRule()

    @Test fun gastoConCentavosYDescripcionVacia() {
        var guardado: Gasto? = null
        compose.setContent {
            Lunes26octTheme {
                AgregarGasto(
                    categorias = listOf(Categoria(1, "Transporte")),
                    onGuardar = { monto, concepto, id -> guardado = Gasto(categoriaId = id, monto = monto, descripcion = concepto); true },
                    onCrearCategoria = {}, onVolver = {}
                )
            }
        }
        compose.onNodeWithContentDescription("Monto").performTextInput("35.50")
        compose.onNodeWithText("Selecciona una categoría").performClick()
        compose.onNodeWithText("Transporte").performClick()
        compose.onNodeWithText("Guardar gasto").performScrollTo().performClick()
        compose.runOnIdle {
            assertNotNull(guardado)
            assertEquals(35.50, guardado!!.monto, 0.0)
            assertEquals("", guardado!!.descripcion)
        }
    }

    @Test fun sinCategoriasOrientaYBloqueaGuardado() {
        var crear = false
        compose.setContent {
            Lunes26octTheme {
                AgregarGasto(emptyList(), { _, _, _ -> false }, { crear = true }, {})
            }
        }
        compose.onNodeWithText("Guardar gasto").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Crear categoria").performScrollTo().performClick()
        compose.runOnIdle { assertTrue(crear) }
    }

    @Test fun inicioIncluyeCategoriasSinGastosYTotal() {
        compose.setContent {
            Lunes26octTheme {
                PantallaPrincipal(
                    gastos = emptyList(),
                    resumen = listOf(ResumenCategoria("Alimentacion", 205.50), ResumenCategoria("Transporte", 35.0), ResumenCategoria("Entretenimiento", 0.0)),
                    categorias = emptyList(), onEditar = {}, onEliminar = {}, mensaje = ""
                )
            }
        }
        compose.onNodeWithText("$ 240.50").assertExists()
        compose.onNodeWithText("Entretenimiento").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("$ 0.00").assertIsDisplayed()
    }

    @Test fun categoriaValidaNombreYGuarda() {
        var nombreGuardado = ""
        compose.setContent {
            Lunes26octTheme {
                NuevaCategoria(onCerrar = {}, onGuardar = { nombre, _ -> nombreGuardado = nombre; true })
            }
        }
        compose.onNodeWithText("Guardar").performClick()
        compose.onNodeWithText("Escribe un nombre.").assertExists()
        compose.onNode(hasSetTextAction()).performTextInput("  Transporte  ")
        compose.onNodeWithText("Guardar").performClick()
        compose.runOnIdle { assertEquals("Transporte", nombreGuardado) }
    }
}
