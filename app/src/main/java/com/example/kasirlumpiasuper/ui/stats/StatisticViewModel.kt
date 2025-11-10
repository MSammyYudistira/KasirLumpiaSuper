package com.example.kasirlumpiasuper.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.example.kasirlumpiasuper.ui.utils.WeekRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _weeksOfMonth = MutableStateFlow<List<WeekRange>>(emptyList())
    val weeksOfMonth: StateFlow<List<WeekRange>> = _weeksOfMonth

    private val _selectedWeek = MutableStateFlow<WeekRange?>(null)
    val selectedWeek: StateFlow<WeekRange?> = _selectedWeek

    private val _dailyRevenue = MutableStateFlow<Map<String, Int>>(emptyMap())
    val dailyRevenue: StateFlow<Map<String, Int>> = _dailyRevenue

    private val _totalWeekly = MutableStateFlow(0)
    val totalWeekly: StateFlow<Int> = _totalWeekly

    private val _averageDaily = MutableStateFlow(0)
    val averageDaily: StateFlow<Int> = _averageDaily

    private val _growthPercent = MutableStateFlow<Float?>(null)
    val growthPercent: StateFlow<Float?> = _growthPercent


    /** 🔹 Generate daftar minggu dalam bulan tertentu */
    fun getWeeksOfMonth(year: Int, month: Int) {
        _weeksOfMonth.value = DateUtils.generateWeeksOfMonth(year, month)
    }

    /** 🔹 Ambil data pendapatan untuk minggu terpilih */
    fun loadWeeklyRevenue(year: Int, month: Int, weekIndex: Int) {
        viewModelScope.launch {
            val weeks = DateUtils.generateWeeksOfMonth(year, month)
            if (weekIndex < 0 || weekIndex >= weeks.size) return@launch
            val weekRange = weeks[weekIndex]
            _selectedWeek.value = weekRange

            val resultMap = mutableMapOf<String, Int>()
            var totalCurrent = 0

            // 🔹 Ambil total pendapatan untuk minggu terpilih
            for (dateKey in weekRange.dateKeys) {
                val revenue = repository.getDailyRevenue(dateKey)
                resultMap[dateKey] = revenue
                totalCurrent += revenue
            }

            _dailyRevenue.value = resultMap.toSortedMap()
            _totalWeekly.value = totalCurrent
            _averageDaily.value =
                if (resultMap.isNotEmpty()) totalCurrent / resultMap.size else 0

            // 🔹 Ambil data minggu sebelumnya (kalau ada)
            if (weekIndex > 0) {
                val prevWeek = weeks[weekIndex - 1]
                var totalPrev = 0
                for (dateKey in prevWeek.dateKeys) {
                    totalPrev += repository.getDailyRevenue(dateKey)
                }

                val growth = calculateGrowth(totalCurrent, totalPrev)
                _growthPercent.value = growth
            } else {
                _growthPercent.value = null
            }
        }
    }


    /** 🔹 Hitung pertumbuhan minggu ini dibanding minggu sebelumnya */
    fun calculateGrowth(currentWeekTotal: Int, prevWeekTotal: Int): Float {
        if (prevWeekTotal == 0) return 0f
        return ((currentWeekTotal - prevWeekTotal) / prevWeekTotal.toFloat()) * 100
    }
}