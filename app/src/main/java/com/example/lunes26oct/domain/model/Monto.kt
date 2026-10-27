package com.example.lunes26oct.domain.model

import java.math.BigDecimal
import java.math.RoundingMode

fun normalizaMonto(monto: Double): Double? {
    if (!monto.isFinite() || monto <= 0.0) return null
    return BigDecimal.valueOf(monto).setScale(2, RoundingMode.HALF_UP)
        .toDouble().takeIf { it > 0.0 }
}

fun leeMonto(texto: String): Double? {
    val limpio = texto.trim().replace(',', '.')
    if (!Regex("[0-9]+(\\.[0-9]+)?").matches(limpio)) return null
    val decimal = limpio.toBigDecimalOrNull() ?: return null
    val valor = decimal.setScale(2, RoundingMode.HALF_UP).toDouble()
    return valor.takeIf { it.isFinite() && it > 0.0 }
}
