package com.matsak.ellicitycompose.dto

import com.matsak.ellicitycompose.dto.System

class SystemsList(var systemsList: List<com.matsak.ellicitycompose.dto.System>) : java.io.Serializable {
    var systems = systemsList

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SystemsList

        if (systemsList != other.systemsList) return false
        if (systems != other.systems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = systemsList.hashCode()
        result = 31 * result + systems.hashCode()
        return result
    }

}