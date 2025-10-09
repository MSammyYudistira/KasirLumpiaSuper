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

    fun timeLabel(millis: Long?): String {
        if (millis == null || millis <= 0) return "-"
        val format = SimpleDateFormat("HH:mm", idLocale)
        return format.format(Date(millis))
    }

    fun dateLabel(millis: Long?): String {
        if (millis == null || millis <= 0) return "-"
        val format = SimpleDateFormat("dd MMMM yyyy", idLocale)
        return format.format(Date(millis))
    }

    fun rupiah(amount: Int?): String {
        if (amount == null) return "Rp 0"
        val numberFormat = NumberFormat.getNumberInstance(idLocale)
        numberFormat.maximumFractionDigits = 0
        return "Rp ${numberFormat.format(amount)}"
    }

    fun prevBusinessDateLabel(currentLabel: String): String? {
        return try {
            val inFmt = SimpleDateFormat("dd MMMM yyyy", idLocale)
            val outFmt = SimpleDateFormat("dd MMMM yyyy", idLocale)
            val date = inFmt.parse(currentLabel) ?: return null
            val cal = Calendar.getInstance().apply { time = date; add(Calendar.DAY_OF_MONTH, -1) }
            outFmt.format(cal.time)
        } catch (_: Exception) { null }
    }
}