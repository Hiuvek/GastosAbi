package com.example.lunes26oct.ui.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

internal fun formatoMonto(monto: BigDecimal): String = "$ " + NumberFormat.getNumberInstance(Locale.forLanguageTag("es-MX")).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
    roundingMode = RoundingMode.HALF_UP
}.format(monto)
