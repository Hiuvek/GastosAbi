package com.example.lunes26oct.ui.util

import java.math.BigDecimal
import java.text.NumberFormat

internal fun formatoMonto(monto: BigDecimal): String = "$ " + NumberFormat.getIntegerInstance().format(monto)
