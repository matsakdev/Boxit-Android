package com.matsak.ellicitycompose.screens.login

data class LoadingState(val status: Status, val message : String? = null) {

    companion object {
        val IDLE = LoadingState(Status.IDLE)
        val LOADING = LoadingState(Status.LOADING)
        val RUNNING = LoadingState(Status.RUNNING)
        val SUCCESS = LoadingState(Status.SUCCESS)
        val FAILED = LoadingState(Status.FAILED)
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED,
        IDLE,
        LOADING
    }
}
