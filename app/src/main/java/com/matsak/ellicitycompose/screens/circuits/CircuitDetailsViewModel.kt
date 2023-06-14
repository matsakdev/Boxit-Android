package com.matsak.ellicitycompose.screens.circuits

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.himanshoe.charty.line.model.LineData
import com.matsak.ellicitycompose.R
import com.matsak.ellicitycompose.dto.Current
import com.matsak.ellicitycompose.dto.Device
import com.matsak.ellicitycompose.dto.Measurement
import com.matsak.ellicitycompose.dto.Voltage
import com.matsak.ellicitycompose.utils.HttpRequester
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CircuitDetailsViewModel : ViewModel() {
    private val _voltage = MutableLiveData<List<LineData>>()
    val voltage = _voltage

    private val _current = MutableLiveData<List<LineData>>()
    val current = _current

    val measurement = _measurements
    val devices = _devices


    private val timeFormatForCharts = DateTimeFormatter.ofPattern("MM:ss")

    private var currentCircuitIdProcessed: Long = 0

    fun getMeasurements(ctx: Context, circuitId: Long) {
        if (currentCircuitIdProcessed != circuitId) {
            stopSendingRequests()
            setupNewCircuitForRequests(circuitId)
        }
        val executor: ExecutorService = Executors.newSingleThreadExecutor()

        executor.execute {
            sendingRequests = true;
            while (sendingRequests) {
                sendUpdateDataRequest(ctx, circuitId)
                sendUpdateDevicesStateRequest(ctx, circuitId)
                Thread.sleep(2500)
            }
            println("STOPPED!")
        }
    }

    companion object {
        var sendingRequests: Boolean = false
        private val _measurements = MutableLiveData<List<Measurement>>()

        private val _devices = MutableLiveData<List<Device>>()

        fun stopSendingRequests() {
            sendingRequests = false
            clearMeasurements()
        }

        private fun clearMeasurements() {
            _measurements.value = listOf()
            _devices.value = listOf()
        }

    }

    private fun setupNewCircuitForRequests(circuitId: Long) {
        currentCircuitIdProcessed = circuitId
    }

    private fun sendUpdateDataRequest(ctx: Context, circuitId: Long) {
        val queue = Volley.newRequestQueue(ctx)
        val url = ctx.getString(R.string.baseURL) + ctx.getString(R.string.getCircuits) + "/" +
                circuitId + ctx.getString(R.string.getMeasurements) + "/previous" +
                if (_measurements.value == null || _measurements.value!!.isEmpty()) "/10"
                else "/2"

        val request = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONArray> { response ->
                successfulRequest(response)
            },
            Response.ErrorListener { response ->
                errorHandler(response);
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                return setHeaders(ctx)
            }
        }
        queue.add(request)
    }

    private fun sendUpdateDevicesStateRequest(ctx: Context, circuitId: Long) {
        val queue = Volley.newRequestQueue(ctx)
        val url =
            ctx.getString(R.string.baseURL) + ctx.getString(R.string.getCircuits) + "/" + circuitId +
                    ctx.getString(R.string.getDevices)

        val request = HttpRequester.sendGetRequestWithEmptyBodyAndProcessJsonArray(
            ctx = ctx,
            url = url,
            onSuccess = { response -> updateDevicesAndStates(response) },
            onError = { error -> HttpRequester.defaultOnError(error, ctx) }
        )
    }

    private fun updateDevicesAndStates(response: JSONArray) {
        val devices: ArrayList<Device> = arrayListOf()
        response.let {
            for (i in 0 until response.length()) {
                val thisDevice: Device = GsonBuilder()
                    .create()
                    .fromJson(
                        response.getJSONObject(i).toString(),
                        Device::class.java
                    )
                devices.add(thisDevice)
            }
        }
        _devices.value = devices
    }

    private fun setHeaders(ctx: Context): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        val token: String? =
            ctx.getSharedPreferences("com.matsak.ellicitycompose", Context.MODE_PRIVATE)
                .getString("token", "")
        val tokenType: String? =
            ctx.getSharedPreferences("com.matsak.ellicitycompose", Context.MODE_PRIVATE)
                .getString("tokenType", "")
        headers["Authorization"] = "$tokenType $token"
        return headers
    }

    fun successfulRequest(response: JSONArray) {
        val measurements: ArrayList<Measurement> = arrayListOf()
        response.let {
            for (i in 0 until response.length()) {
                val responseMeasurement = response.getJSONObject(i);
                val measurement = Measurement(
                    convertTime(responseMeasurement.getString("time")),
                    getVoltage(responseMeasurement.getJSONObject("voltage")),
                    getCurrent(responseMeasurement.getJSONObject("current"))
                )
                measurements.add(measurement)
            }
        }
        updateMeasurements(measurements)
    }

    private fun updateMeasurements(measurements: ArrayList<Measurement>) {
        var previousMeasurements: List<Measurement>? = _measurements.value
        var newMeasurements: ArrayList<Measurement> = arrayListOf()
        if (previousMeasurements != null) {
            var newElements: List<Measurement> = measurements.filter {
                !previousMeasurements
                    .map { measurement -> measurement.time }
                    .contains(it.time)
            }
            previousMeasurements.forEach {
                newMeasurements.add(it)
            }
            newElements.forEach {
                newMeasurements.add(it)
            }
        } else {
            newMeasurements = measurements
        }
        newMeasurements.sortBy { list -> list.time }
        val elementsToRemoveCount = if (newMeasurements.size > 10) { //todo final constant
            newMeasurements.size - 10
        } else {
            0
        }
        newMeasurements.subList(0, elementsToRemoveCount).clear()
        _measurements.value = newMeasurements
        updateVoltageMeasurements()
        updateCurrentMeasurements()
    }

    private fun updateVoltageMeasurements() {
        _voltage.value = _measurements.value?.map { measurement ->
            LineData(
                measurement.time.format(timeFormatForCharts),
                measurement.voltage.value.toFloat()
            )
        }
    }

    private fun updateCurrentMeasurements() {
        _current.value = _measurements.value?.map { measurement ->
            LineData(
                measurement.time.format(timeFormatForCharts),
                measurement.current.value.toFloat()
            )
        }
    }

    private fun convertTime(time: String): LocalDateTime {
        val dateFormat: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        return LocalDateTime.parse(time, dateFormat);
    }

    private fun getVoltage(voltageJson: JSONObject): Voltage {
        return Voltage(voltageJson.getDouble("value"))
    }

    private fun getCurrent(currentJson: JSONObject): Current {
        return Current(currentJson.getDouble("value"))
    }

    data class AuthResponse(val accessToken: String, val tokenType: String)

    fun errorHandler(response: VolleyError?) {
        println(response)
    }

    fun updateDeviceState(ctx: Context, device: Device) {
        val action = if (device.working) "on" else "off"
        HttpRequester.sendPostRequestWithoutBodyAndProcessResult(
            ctx = ctx,
            url = ctx.getString(R.string.baseURL) + ctx.getString(R.string.getDevices) +
                    "/${device.id}/" + action,
            onSuccess = { response -> updateDeviceSuccess(ctx, device, action, response) },
            onError = { error -> HttpRequester.defaultOnError(error, ctx) }
        )
    }

    private fun updateDeviceSuccess(
        ctx: Context,
        device: Device,
        action: String,
        response: JSONObject
    ) {
        if (response.getBoolean("success")) {
            Toast.makeText(ctx, "Device ${device.name} $action", Toast.LENGTH_SHORT).show()
            sendUpdateDevicesStateRequest(ctx, currentCircuitIdProcessed)
        }
        else {
            Toast.makeText(ctx, "Device cannot change state now", Toast.LENGTH_SHORT).show()
        }
    }
}
