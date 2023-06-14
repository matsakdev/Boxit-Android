package com.matsak.ellicitycompose.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData
import com.matsak.ellicitycompose.dto.System

@Composable
fun StatisticsContent(
    navController: NavHostController,
    viewModel: StatisticsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val statistics by viewModel.statistics.observeAsState()
        viewModel.loadStatistics(navController.context);

        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
            if (statistics != null) {
                itemsIndexed(statistics!!.systemsStatistics.keys.toList()) { key, item ->
                    SystemStatisticsCard(item, statistics!!)
                }
            } else println(viewModel.statistics.value)
        }
    }
}

@Composable
fun SystemStatisticsCard(item: System, statistics: Statistics): Unit {
    OutlinedCard(modifier = Modifier
        .padding(10.dp)
        .wrapContentHeight()) {
        if (statistics.systemsStatistics[item] != null) {
            if (statistics.systemsStatistics[item]!!.get(0) != null) {
                StatisticsBox(statistics.systemsStatistics[item]!!.get(0))
            }
            if (statistics.systemsStatistics[item]!!.size > 1) {
                Divider(modifier = Modifier.padding(5.dp), color = Color.DarkGray, thickness = 1.dp)
                StatisticsBox(statistics.systemsStatistics[item]!!.get(1))
            }
            if (statistics.systemsStatistics[item]!!.size > 2) {
                Divider(modifier = Modifier.padding(5.dp), color = Color.DarkGray, thickness = 1.dp)
                StatisticsBox(statistics.systemsStatistics[item]!!.get(2))
            }
        }
    }
}

@Composable
fun StatisticsBox(stats: SystemStatistics) {
    val chartsData = arrayListOf<BarData>()
    stats.power.keys.forEach { key ->
        chartsData.add(BarData(key, stats.power[key]!!.toFloat()))
    }
    val statsType: StatisticsType = defineStatsType(stats)

    println(chartsData)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            textAlign = TextAlign.Center,
            text = if (statsType == StatisticsType.PREV_YEAR) "Previous year statistics"
            else if (statsType == StatisticsType.PREV_MONTH) "Previous month statistics"
            else "Current month statistics",
            color = if (statsType == StatisticsType.PREV_YEAR) Color.Blue
            else if (statsType == StatisticsType.PREV_MONTH) Color.Yellow
            else Color.Magenta,
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Most powerful circuit: " + if (stats.mostPowerfulCircuit.circuit != null) stats.mostPowerfulCircuit.circuit.name else "No data found"
        )
        Text(
            modifier = Modifier.padding(vertical = 5.dp),
            fontWeight = FontWeight.Bold,
            text = "Cost: " + stats.cost
        )
        if (chartsData.size != 0) {
            Box(
                modifier = Modifier.height(250.dp)
            ) {
                BarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(32.dp),
                    onBarClick = {},
                    colors = listOf(Color.Green, Color.Yellow),
                    barData = chartsData
                )
            }
        } else {
            Text(
                modifier = Modifier.padding(vertical = 5.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                text = "NO DATA ABOUT MEASUREMENTS"
            )
        }
        if (stats.predictedCost != stats.cost.replace(",", ".")
                .toDouble() && stats.predictedCost != 0.toDouble()
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Predicted cost: " + String.format("%.2f", stats.predictedCost),
                fontStyle = FontStyle.Italic
            )
        }
    }
}

fun defineStatsType(stats: SystemStatistics): StatisticsType {
    return if (stats.predictedCost != 0.toDouble()
        && stats.predictedCost != stats.cost.replace(",", ".").toDouble()
    ) {
        StatisticsType.CURR_MONTH
    } else if (stats.power.keys.any { it.matches("^[a-zA-Z]*$".toRegex()) }) {
        StatisticsType.PREV_YEAR
    } else StatisticsType.PREV_MONTH
}
