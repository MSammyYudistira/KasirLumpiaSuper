package com.example.kasirlumpiasuper.ui.utils

import android.icu.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val idLocale = Locale("id", "ID")

//    fun setManualDate(date: String) {
//        manualDateLabel = date
//    }
//
//    fun getBusinessDateLabel(): String {
//        manualDateLabel?.let { return it } // ✅ pakai tanggal manual kalau ada
//
//        val cal = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
//        return dateFormat.format(cal.time)
//    }

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
}

object BusinessDateManager {
    private var lockedDateLabel: String? = null
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    fun getBusinessDateLabel(): String =
        lockedDateLabel ?: dateFormat.format(Calendar.getInstance().time)

    fun getCurrentSystemDateLabel(): String =
        dateFormat.format(Calendar.getInstance().time)

    fun lockTo(dateLabel: String) { lockedDateLabel = dateLabel }

    fun releaseLock() { lockedDateLabel = null }

    fun isLocked(): Boolean = lockedDateLabel != null

    fun getLockedDate(): String? = lockedDateLabel
}
