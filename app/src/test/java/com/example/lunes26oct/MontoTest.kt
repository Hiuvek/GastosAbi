package com.example.lunes26oct

import com.example.lunes26oct.domain.model.leeMonto
import com.example.lunes26oct.domain.model.normalizaMonto
import com.example.lunes26oct.ui.util.formatoMonto
import org.junit.Assert.*
import org.junit.Test

class MontoTest {
    @Test fun aceptaCentavosYRedondea() {
        assertEquals(85.50, leeMonto("85.50")!!, 0.0)
        assertEquals(85.50, leeMonto(" 85,50 ")!!, 0.0)
        assertEquals(1.01, leeMonto("1.005")!!, 0.0)
        assertEquals(1.01, normalizaMonto(1.005)!!, 0.0)
        assertEquals("$ 240.50", formatoMonto("240.50".toBigDecimal()))
        assertEquals("$ 0.00", formatoMonto("0".toBigDecimal()))
    }
    @Test fun rechazaMontosInvalidos() {
        listOf("", "abc", "-1", "0", "0.004", "NaN", "Infinity", "1,200.50").forEach {
            assertNull(it, leeMonto(it))
        }
        listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, -1.0, 0.0, 0.004).forEach {
            assertNull(normalizaMonto(it))
        }
    }
}
