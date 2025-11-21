package com.example.kasirlumpiasuper.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.firestore.FirestoreRepository
import com.example.kasirlumpiasuper.data.firestore.RecapRepository
import com.example.kasirlumpiasuper.helper.date.DateUtils
import com.example.kasirlumpiasuper.helper.recap.RecapUtils
import com.example.kasirlumpiasuper.helper.date.WeekRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class StatisticViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _isLoadingChart = MutableStateFlow(false)
    val isLoadingChart: StateFlow<Boolean> = _isLoadingChart

    private val _isLoadingGrowth = MutableStateFlow(false)
    val isLoadingGrowth: StateFlow<Boolean> = _isLoadingGrowth

    private val _weeksOfMonth = MutableStateFlow<List<WeekRange>>(emptyList())

    private val _selectedWeek = MutableStateFlow<WeekRange?>(null)

    private val _dailyRevenue = MutableStateFlow<Map<String, Int>>(emptyMap())

    private val _totalWeekly = MutableStateFlow(0)

    private val _averageDaily = MutableStateFlow(0)

    private val _growthPercent = MutableStateFlow<Float?>(null)

    fun loadRevenueRange(
        startDate: Date,
        onResult: (Map<String, Int>) -> Unit
    ) {
        viewModelScope.launch {
            _isLoadingChart.value = true

            val keys = DateUtils.get7DayKeysFrom(startDate)
            val income = mutableMapOf<String, Int>()

            for (key in keys) {
                try {
                    // 🔥 GLOBAL REVENUE
                    val dailyRevenue = repository.getDailyRevenueGlobal(key)
                    income[key] = dailyRevenue
                } catch (_: Exception) {
                    income[key] = 0
                }
            }

            _isLoadingChart.value = false
            onResult(income)
        }
    }

    fun loadGrowthData(periodDays: Int, onResult: (Map<String, Float>) -> Unit) {
        viewModelScope.launch {
            _isLoadingGrowth.value = true

            val keys = if (periodDays == 30)
                DateUtils.get30DayKeysOfCurrentMonth()
            else
                DateUtils.getLastNDaysKeys(periodDays)

            val growthMap = mutableMapOf<String, Float>()
            var prevRevenue: Int? = null

            for (key in keys) {
                try {
                    // 🔥 GLOBAL REVENUE
                    val currentRevenue = repository.getDailyRevenueGlobal(key)

                    val growth = when {
                        prevRevenue == null || prevRevenue == 0 -> 0f
                        currentRevenue == 0 -> 0f
                        else -> ((currentRevenue - prevRevenue!!) / prevRevenue!!.toFloat()) * 100f
                    }

                    growthMap[key] = growth
                    prevRevenue = currentRevenue

                } catch (_: Exception) {
                    growthMap[key] = 0f
                }
            }

            _isLoadingGrowth.value = false
            onResult(growthMap.toSortedMap())
        }
    }


    fun getWeeksOfMonth(year: Int, month: Int) {
        _weeksOfMonth.value = DateUtils.generateWeeksOfMonth(year, month)
    }

    fun loadWeeklyRevenue(year: Int, month: Int, weekIndex: Int) {
        viewModelScope.launch {

            val weeks = DateUtils.generateWeeksOfMonth(year, month)
            if (weekIndex < 0 || weekIndex >= weeks.size) return@launch

            val selected = weeks[weekIndex]
            _selectedWeek.value = selected

            val resultMap = mutableMapOf<String, Int>()
            var totalCurrent = 0

            // 🔥 GLOBAL WEEKLY REVENUE
            for (dateKey in selected.dateKeys) {
                val revenue = repository.getDailyRevenueGlobal(dateKey)
                resultMap[dateKey] = revenue
                totalCurrent += revenue
            }

            val sorted = resultMap.toSortedMap()
            _dailyRevenue.value = sorted

            _totalWeekly.value = totalCurrent
            _averageDaily.value =
                if (sorted.isNotEmpty()) totalCurrent / sorted.size else 0


            // =====================================================
            // Growth dibanding minggu sebelumnya
            // =====================================================
            if (weekIndex > 0) {
                val prevWeek = weeks[weekIndex - 1]
                var totalPrev = 0

                for (dateKey in prevWeek.dateKeys) {
                    totalPrev += repository.getDailyRevenueGlobal(dateKey)
                }

                _growthPercent.value = calculateGrowth(totalCurrent, totalPrev)
            } else {
                _growthPercent.value = null
            }
        }
    }


    fun calculateGrowth(currentWeekTotal: Int, prevWeekTotal: Int): Float {
        if (prevWeekTotal == 0) return 0f
        return ((currentWeekTotal - prevWeekTotal) / prevWeekTotal.toFloat()) * 100
    }
}