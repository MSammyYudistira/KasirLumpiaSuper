package com.example.kasirlumpiasuper.ui.utils

import java.util.Calendar
import java.util.Locale

object DateUtils {
    fun getBusinessDate(): String {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if (hour < 5) cal.add(Calendar.DAY_OF_MONTH, -1)

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day)
    }

}