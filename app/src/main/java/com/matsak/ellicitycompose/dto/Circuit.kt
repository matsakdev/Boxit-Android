package com.matsak.ellicitycompose.dto

data class Circuit(
    val id: Long,
    var name: String,
    var system: com.matsak.ellicitycompose.dto.System
)
