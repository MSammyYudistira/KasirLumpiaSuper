package com.example.kasirlumpiasuper.ui.utils

import android.icu.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val idLocale = Locale("id", "ID")

    fun getBusinessDateLabel(): String {
        val cal = Calendar.getInstance() // Selalu ambil tanggal hari ini
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", idLocale)
        return dateFormat.format(cal.time)
    }

    /** KEY untuk Firestore path: yyyy-MM-dd (contoh: 2025-09-29) */
    fun getBusinessDateKey(): String {
        val cal = Calendar.getInstance() // reset 00:00 otomatis oleh sistem
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return dateFormat.format(cal.time)
    }

    fun labelFromKey(key: String): String {
        val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outFormat = SimpleDateFormat("dd MMMM yyyy", idLocale)
        return try {
            outFormat.format(inFormat.parse(key)!!)
        } catch (_: ParseException) {
            key
        }
    }

    fun keyFromLabel(label: String): String? {
        return try {
            val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outFormat = SimpleDateFormat("dd MMMM yyyy", idLocale)
            outFormat.format(inFormat.parse(label)!!)
        } catch (_: Exception) {
            null
        }
    }

    fun timeLabel(millis: Long?): String {
        if (millis == null || millis <= 0) return "-"
        val format = SimpleDateFormat("HH:mm", idLocale)
        return format.format(Date(millis))
    }

    fun rupiah(amount: Int?): String {
        if (amount == null) return "Rp 0"
        val numberFormat = NumberFormat.getNumberInstance(idLocale)
        numberFormat.maximumFractionDigits = 0
        return "Rp ${numberFormat.format(amount)}"
    }

}