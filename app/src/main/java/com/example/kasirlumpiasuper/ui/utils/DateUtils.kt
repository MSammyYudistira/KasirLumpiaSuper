package com.example.kasirlumpiasuper.ui.utils

import android.icu.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeekRange(
    val label: String,
    val startDate: String,
    val endDate: String,
    val dateKeys: List<String>
)

object DateUtils {
    private val idLocale = Locale("id", "ID")

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

    fun generateWeeksOfMonth(year: Int, month: Int): List<WeekRange> {
        val cal = Calendar.getInstance(Locale("id", "ID"))
        cal.set(year, month, 1)

        val result = mutableListOf<WeekRange>()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

        var weekIndex = 1
        while (cal.get(Calendar.MONTH) == month) {
            val start = cal.time
            val dateKeys = mutableListOf<String>()
            repeat(7) {
                if (cal.get(Calendar.MONTH) != month) return@repeat
                dateKeys.add(dateFormat.format(cal.time))
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }

            val end = displayFormat.parse(dateKeys.last()) ?: start
            val label = "Minggu ke-$weekIndex"
            result.add(
                WeekRange(
                    label = label,
                    startDate = displayFormat.format(start),
                    endDate = displayFormat.format(end),
                    dateKeys = dateKeys
                )
            )
            weekIndex++
        }

        return result
    }

    fun getDayName(dateString: String): String {
        return try {
            val format = SimpleDateFormat("dd MMMM yyyy", idLocale)
            val date = format.parse(dateString)
            val dayFormat = SimpleDateFormat("EEE", idLocale) // contoh output: "Sen", "Sel", "Rab"
            dayFormat.format(date ?: Date())
        } catch (e: Exception) {
            "-"
        }
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


