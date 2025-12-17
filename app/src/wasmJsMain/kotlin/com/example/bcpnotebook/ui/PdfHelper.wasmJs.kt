package com.example.bcpnotebook.ui
import kotlinx.browser.window

actual fun generateNotePdf(title: String, content: String, summary: String) {
    window.alert("PDF Export is coming soon for Web! Note Saved Successfully.")
}
