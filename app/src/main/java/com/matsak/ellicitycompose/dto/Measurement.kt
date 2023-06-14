package com.matsak.ellicitycompose.dto

import java.time.LocalDateTime

data class Measurement(
    val time: LocalDateTime,
    val voltage: Voltage,
    val current: Current
)