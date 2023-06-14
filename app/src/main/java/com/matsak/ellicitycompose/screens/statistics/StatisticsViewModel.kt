package com.matsak.ellicitycompose.screens.statistics

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.matsak.ellicitycompose.R
import com.matsak.ellicitycompose.dto.System
import com.matsak.ellicitycompose.screens.circuits.CircuitViewModel
import com.matsak.ellicitycompose.utils.HttpRequester
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StatisticsViewModel : ViewModel() {
    private var _statistics = MutableLiveData<Statistics>()
    var statistics = _statistics

    private var _systemsList = MutableLiveData<List<com.matsak.ellicitycompose.dto.System>>()

    fun loadStatistics(context: Context) {
        getAllSystems(context)
    }

    private fun getStatisticsForEachSystem(context: Context) {
        val executor: ExecutorService = Executors.newSingleThreadExecutor();
        executor.execute {
            _systemsList.value?.forEach { system ->
                run {
                    getPreviousYearStatistics(system, context)
                    getPreviousMonthStatistics(system, context)
                    getCurrentMonthStatistics(system, context)
                    Thread.sleep(2000)
                }
            }
        }
    }

    private fun getPreviousYearStatistics(
        system: com.matsak.ellicitycompose.dto.System,
        context: Context
    ) {
        val executor: ExecutorService = Executors.newSingleThreadExecutor();
        executor.execute {
            HttpRequester.sendGetRequestWithEmptyBodyAndProcessJsonObject(
                ctx = context,
                url = context.getString(R.string.baseURL) +
                        context.getString(R.string.systemsURL) +
                        "/" + system.id +
                        context.getString(R.string.statisticsURL) +
                        context.getString(R.string.lastYearStatisticsURL),
                onSuccess = { response ->
                    addSystemStatistics(response, system)
                },
                onError = { error ->
                    println(error)
                    HttpRequester.defaultOnError(error, context)
                }
            )
        }
    }

    private fun addSystemStatistics(
        response: JSONObject,
        system: System
    ) {
        val statisticsDto =
            GsonBuilder().create().fromJson(response.toString(), SystemStatistics::class.java)
        updateStatistics(system, statisticsDto);
    }

    private fun updateStatistics(system: System, statisticsDto: SystemStatistics?) {
        var statisticsObject = _statistics.value
        if (statisticsObject == null) {
            _statistics.value = Statistics(mapOf())
            statisticsObject = _statistics.value
        }
        var allStatistics = statisticsObject?.systemsStatistics
        if (allStatistics == null) {
            allStatistics = mapOf()
        }

        var listStatisticsForCurrentSystem = allStatistics.get(system)
        if (listStatisticsForCurrentSystem == null) listStatisticsForCurrentSystem = listOf()

        if (statisticsDto != null) {
            listStatisticsForCurrentSystem =
                listStatisticsForCurrentSystem.plus(statisticsDto)
        } else return

        allStatistics = allStatistics.plus(Pair(system, listStatisticsForCurrentSystem))

        _statistics.value = Statistics(allStatistics)
    }

    private fun getPreviousMonthStatistics(
        system: com.matsak.ellicitycompose.dto.System,
        context: Context
    ) {
        val executor: ExecutorService = Executors.newSingleThreadExecutor();
        executor.execute {
            HttpRequester.sendGetRequestWithEmptyBodyAndProcessJsonObject(
                ctx = context,
                url = context.getString(R.string.baseURL) +
                        context.getString(R.string.systemsURL) +
                        "/" + system.id +
                        context.getString(R.string.statisticsURL) +
                        context.getString(R.string.lastMonthStatistics),
                onSuccess = { response -> addSystemStatistics(response, system) },
                onError = { error -> HttpRequester.defaultOnError(error, context) }
            )
        }
    }

    private fun getCurrentMonthStatistics(
        system: com.matsak.ellicitycompose.dto.System,
        context: Context
    ) {
        val executor: ExecutorService = Executors.newSingleThreadExecutor();
        executor.execute {
            HttpRequester.sendGetRequestWithEmptyBodyAndProcessJsonObject(
                ctx = context,
                url = context.getString(R.string.baseURL) +
                        context.getString(R.string.systemsURL) +
                        "/" + system.id +
                        context.getString(R.string.statisticsURL) +
                        context.getString(R.string.currentMonthStatistics),
                onSuccess = { response -> addSystemStatistics(response, system) },
                onError = { error -> HttpRequester.defaultOnError(error, context) }
            )
        }
    }

    private fun getAllSystems(context: Context) {
        HttpRequester.sendGetRequestWithEmptyBodyAndProcessJsonArray(
            ctx = context,
            url = context.getString(R.string.baseURL) + context.getString(R.string.systemsURL),
            onSuccess = { response ->
                successfulGetAllSystemsRequest(response, context)
            },
            onError = { error ->
                HttpRequester.defaultOnError(error, context)
            }
        )
    }

    private fun successfulGetAllSystemsRequest(response: JSONArray, context: Context) {
        val systemsList: ArrayList<com.matsak.ellicitycompose.dto.System> = arrayListOf()
        response.let {
            for (i in 0 until response.length()) {
                val responseSystem = response.getJSONObject(i);
                val system = com.matsak.ellicitycompose.dto.System(
                    responseSystem.getLong("id"),
                    responseSystem.getString("name"),
                    responseSystem.getString("passKey")
                )
                systemsList.add(system)
            }
        }
        updateSystemsList(systemsList)

        getStatisticsForEachSystem(context)
    }

    private fun updateSystemsList(systemsList: ArrayList<com.matsak.ellicitycompose.dto.System>) {
        this._systemsList.value = systemsList
    }

}
