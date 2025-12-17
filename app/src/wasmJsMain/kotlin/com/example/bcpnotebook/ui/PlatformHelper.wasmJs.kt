package com.example.bcpnotebook.ui
import kotlinx.browser.window

actual fun showToast(message: String) {
    window.alert(message)
}
