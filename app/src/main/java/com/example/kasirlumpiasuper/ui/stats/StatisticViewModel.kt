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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _isLoadingChart = MutableStateFlow(false)
    val isLoadingChart: StateFlow<Boolean> = _isLoadingChart

    private val _isLoadingGrowth = MutableStateFlow(false)
    val isLoadingGrowth: StateFlow<Boolean> = _isLoadingGrowth

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

    //Dual Bar Chart
    private val _incomeData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val incomeData: StateFlow<Map<String, Int>> = _incomeData

    private val _cashData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val cashData: StateFlow<Map<String, Int>> = _cashData

    fun loadRevenueAndExpenseRange(
        startDate: Date,
        onResult: (Map<String, Int>, Map<String, Int>) -> Unit
    ) {
        viewModelScope.launch {
            _isLoadingChart.value = true

            val repo = RecapRepository()
            val keys = DateUtils.get7DayKeysFrom(startDate)

            val income = mutableMapOf<String, Int>()
            val expense = mutableMapOf<String, Int>()

            for (key in keys) {
                try {
                    // Ambil semua data dari Firestore
                    val orders = repo.getOrdersByDate(key)
                    val stockItems = repo.getStockItemsByDate(key)
                    val stockMeta = repo.getStockMetaByDate(key)
                    val recapInput = repo.getRecapInputByDate(key)

                    // Hitung DailyRecap lengkap
                    val dailyRecap = RecapUtils.compute(
                        RecapUtils.Inputs(
                            dateLabel = key,
                            orders = orders,
                            stockItems = stockItems,
                            stockMeta = stockMeta,
                            recapInput = recapInput
                        )
                    )

                    // Ambil total pendapatan & pengeluaran (ExpenseSummary.sum)
                    income[key] = dailyRecap.grossSection.sum1
                    expense[key] = dailyRecap.expenseSummary.sum

                } catch (e: Exception) {
                    income[key] = 0
                    expense[key] = 0
                }
            }

            _isLoadingChart.value = false
            onResult(income, expense)
        }
    }




    fun loadGrowthData(periodDays: Int, onResult: (Map<String, Float>) -> Unit) {
        viewModelScope.launch {
            _isLoadingGrowth.value = true

            val repo = RecapRepository()
            // ✅ kunci perbaikan: pilih sumber keys yang benar
            val keys = if (periodDays == 30)
                DateUtils.get30DayKeysOfCurrentMonth()
            else
                DateUtils.getLastNDaysKeys(periodDays)

            val growthMap = mutableMapOf<String, Float>()
            var prevRevenue: Int? = null  // biar titik pertama selalu 0%

            for (key in keys) {
                try {
                    val orders = repo.getOrdersByDate(key)
                    val stockItems = repo.getStockItemsByDate(key)
                    val stockMeta = repo.getStockMetaByDate(key)
                    val recapInput = repo.getRecapInputByDate(key)

                    val recap = RecapUtils.compute(
                        RecapUtils.Inputs(
                            dateLabel = key,
                            orders = orders,
                            stockItems = stockItems,
                            stockMeta = stockMeta,
                            recapInput = recapInput
                        )
                    )

                    val currentRevenue = recap.grossSection.sum1

                    // ✅ Hitung pertumbuhan harian dengan perlakuan netral jika belum ada transaksi
                    val growth = when {
                        prevRevenue == null || prevRevenue == 0 -> 0f
                        currentRevenue == 0 -> 0f // belum ada transaksi → netral
                        else -> ((currentRevenue - prevRevenue!!) / prevRevenue!!.toFloat()) * 100f
                    }

                    growthMap[key] = growth
                    prevRevenue = currentRevenue
                } catch (_: Exception) {
                    growthMap[key] = 0f
                }
            }

            _isLoadingGrowth.value = false
            onResult(growthMap.toSortedMap()) // urut lama → baru (cocok untuk sumbu X)
        }
    }





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