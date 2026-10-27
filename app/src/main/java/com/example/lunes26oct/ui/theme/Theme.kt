package com.example.lunes26oct.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PaletaSistema = lightColorScheme(
    primary = MoradoPrincipal,
    onPrimary = Superficie,
    primaryContainer = MoradoSuave,
    onPrimaryContainer = TextoPrincipal,
    secondary = MoradoPrincipal,
    onSecondary = Superficie,
    secondaryContainer = MoradoSuave,
    onSecondaryContainer = TextoPrincipal,
    background = FondoGeneral,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = FondoGeneral,
    onSurfaceVariant = TextoSecundario,
    surfaceTint = MoradoPrincipal,
    outline = Borde,
    outlineVariant = Borde,
    error = ColorError,
    onError = Superficie
)

@Composable
fun Lunes26octTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PaletaSistema, typography = Typography, content = content)
}
