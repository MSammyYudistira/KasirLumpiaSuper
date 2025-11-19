package com.example.kasirlumpiasuper.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.example.kasirlumpiasuper.data.repository.RecapRepository
import com.example.kasirlumpiasuper.ui.utils.DateUtils
import com.example.kasirlumpiasuper.ui.utils.RecapUtils
import com.example.kasirlumpiasuper.ui.utils.WeekRange
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

            val repo = RecapRepository()
            val keys = DateUtils.get7DayKeysFrom(startDate)

            val income = mutableMapOf<String, Int>()

            for (key in keys) {
                try {
                    // Ambil recap penuh
                    val orders = repo.getOrdersByDate(key)
                    val stockItems = repo.getStockItemsByDate(key)
                    val stockMeta = repo.getStockMetaByDate(key)
                    val recapInput = repo.getRecapInputByDate(key)

                    val dailyRecap = RecapUtils.compute(
                        RecapUtils.Inputs(
                            dateLabel = key,
                            orders = orders,
                            stockItems = stockItems,
                            stockMeta = stockMeta,
                            recapInput = recapInput
                        )
                    )

                    // Hanya pendapatan
                    income[key] = dailyRecap.grossSection.sum1

                } catch (e: Exception) {
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

            val repo = RecapRepository()

            val keys = if (periodDays == 30)
                DateUtils.get30DayKeysOfCurrentMonth()
            else
                DateUtils.getLastNDaysKeys(periodDays)

            val growthMap = mutableMapOf<String, Float>()
            var prevRevenue: Int? = null

            for (key in keys) {
                try {
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
            val weekRange = weeks[weekIndex]
            _selectedWeek.value = weekRange

            val resultMap = mutableMapOf<String, Int>()
            var totalCurrent = 0

            for (dateKey in weekRange.dateKeys) {
                val revenue = repository.getDailyRevenueGlobal(dateKey)
                resultMap[dateKey] = revenue
                totalCurrent += revenue
            }

            _dailyRevenue.value = resultMap.toSortedMap()
            _totalWeekly.value = totalCurrent
            _averageDaily.value =
                if (resultMap.isNotEmpty()) totalCurrent / resultMap.size else 0

            if (weekIndex > 0) {
                val prevWeek = weeks[weekIndex - 1]
                var totalPrev = 0

                for (dateKey in prevWeek.dateKeys) {
                    totalPrev += repository.getDailyRevenueGlobal(dateKey)
                }

                val growth = calculateGrowth(totalCurrent, totalPrev)
                _growthPercent.value = growth
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