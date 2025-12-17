package com.example.bcpnotebook.ui

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.bcpnotebook.R

// আমাদের কাস্টম ফন্টগুলোকে এখানে সংজ্ঞায়িত করা হয়েছে
val Lato = FontFamily(
    Font(R.font.lato_regular, FontWeight.Normal)
)

val Merriweather = FontFamily(
    Font(R.font.merriweather_36ptregular, FontWeight.Normal)
)

// ফন্টের তথ্য ধরে রাখার জন্য একটি ডেটা ক্লাস
data class FontInfo(val name: String, val fontFamily: FontFamily)

// অ্যাপে ব্যবহার করার জন্য ফন্টের তালিকা
val appFonts = listOf(
    FontInfo("Default", FontFamily.Default),
    FontInfo("Serif", FontFamily.Serif),
    FontInfo("Lato", Lato),
    FontInfo("Merriweather", Merriweather)
)