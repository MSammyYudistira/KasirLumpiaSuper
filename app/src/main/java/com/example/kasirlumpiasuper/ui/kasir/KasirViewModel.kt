package com.example.kasirlumpiasuper.ui.kasir

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class KasirViewModel : ViewModel() {

    private val _stockFilledToday = MutableStateFlow(false)
    val stockFilledToday: StateFlow<Boolean> = _stockFilledToday

    fun setStockFilled(filled: Boolean) {
        _stockFilledToday.value = filled
    }

    fun checkStockForToday() {
        val now = LocalDateTime.now()
        val businessDate = getBusinessDate(now)

        val stockHariIniAda = false
        _stockFilledToday.value = stockHariIniAda
    }

    fun getBusinessDate (now: LocalDateTime): LocalDate {
        val cutoff = now.toLocalDate().atTime(5,0)
        return if (now.isBefore(cutoff)) {
            now.toLocalDate().minusDays(1)
        } else {
            now.toLocalDate()
        }
    }
}