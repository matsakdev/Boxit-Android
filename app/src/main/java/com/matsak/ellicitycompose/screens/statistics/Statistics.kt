package com.matsak.ellicitycompose.screens.statistics

data class Statistics (
    var systemsStatistics: Map<com.matsak.ellicitycompose.dto.System, List<SystemStatistics>>
    )
