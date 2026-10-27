package com.example.lunes26oct.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

// Reemplaza los PNG de res-pantallas conservando sus nombres.
@Composable
internal fun IconoGasto(@DrawableRes recurso: Int) {
    Image(
        painter = painterResource(recurso),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}
