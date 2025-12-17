package com.example.bcpnotebook.ui
import android.widget.Toast
import com.example.bcpnotebook.MyApplication 

actual fun showToast(message: String) {
    // এখানে 'actual' কিউওয়ার্ডটি গুরুত্বপূর্ণ
    // আপনার প্রজেক্টে যদি গ্লোবাল কনটেক্সট থাকে সেটি ব্যবহার করবেন
    println("Android Toast: $message")
}
