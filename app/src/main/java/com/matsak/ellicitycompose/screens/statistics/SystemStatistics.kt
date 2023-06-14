package com.matsak.ellicitycompose.screens.statistics

import android.gesture.Prediction
import com.matsak.ellicitycompose.dto.Circuit
import com.matsak.ellicitycompose.dto.CircuitStatisticsDto

data class SystemStatistics(
    val cost: String,
    val power: Map<String, Double>,
    val mostPowerfulCircuit: CircuitStatisticsDto,
    val predictedCost: Double = cost.replace(",", ".").toDouble())
