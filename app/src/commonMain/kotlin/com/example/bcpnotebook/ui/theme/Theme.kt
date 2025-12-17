package com.example.bcpnotebook.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BCPNotebookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = SoftBlue,
            background = BackgroundLight,
            surface = SurfaceWhite
        ),
        content = content
    )
}
