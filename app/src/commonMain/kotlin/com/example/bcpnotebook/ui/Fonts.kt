package com.example.bcpnotebook.ui

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

data class FontInfo(val name: String, val fontFamily: FontFamily)

val appFonts = listOf(
    FontInfo("Default", FontFamily.Default),
    FontInfo("Serif", FontFamily.Serif),
    FontInfo("Monospace", FontFamily.Monospace)
)
