package com.matsak.ellicitycompose.screens.circuits

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.matsak.ellicitycompose.R
import com.matsak.ellicitycompose.dto.Circuit
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CircuitViewModel : ViewModel() {
    private val _circuits = MutableLiveData<List<Circuit>>()
    val circuits = _circuits

    private var currentSystemIdInProcessing: Long = 0
    private var isCircuitsUpdating: Boolean = false

    fun getCircuits(ctx: Context, systemId: Long) {
        if (currentSystemIdInProcessing != systemId) {
            stopSendingRequests()
            setupNewSystemForRequests(systemId)
        }
        isCircuitsUpdating = true
        val executor: ExecutorService = Executors.newSingleThreadExecutor();
        executor.execute {
            sendingRequests = true
            while (sendingRequests) {
                sendRequest(ctx, systemId)
                Thread.sleep(5000)
            }
            println("STOPPED!")
        }
    }

    companion object{
        var sendingRequests: Boolean = false

        fun stopSendingRequests() {
            sendingRequests = false
        }
    }

    private fun stopSendingRequests() {
        isCircuitsUpdating = false
        _circuits.value = listOf()
    }

    private fun setupNewSystemForRequests(systemId: Long) {
        currentSystemIdInProcessing = systemId
    }

    private fun sendRequest(ctx: Context, systemId: Long) {
        val queue = Volley.newRequestQueue(ctx)
        val url =
            ctx.getString(R.string.baseURL) + ctx.getString(R.string.systemsURL) + "/" + systemId + ctx.getString(
                R.string.getUsersCircuitsURL
            )

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

    private fun setHeaders(ctx: Context): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        val token: String? =
            ctx.getSharedPreferences("com.matsak.ellicitycompose", Context.MODE_PRIVATE)
                .getString("token", "");
        val tokenType: String? =
            ctx.getSharedPreferences("com.matsak.ellicitycompose", Context.MODE_PRIVATE)
                .getString("tokenType", "");
        headers["Authorization"] = "$tokenType $token"
        return headers
    }

    fun successfulRequest(response: JSONArray) {
        val circuitsList: ArrayList<Circuit> = arrayListOf()
        response.let {
            for (i in 0 until response.length()) {
                val responseCircuit = response.getJSONObject(i);
                val circuitObject = Circuit(
                    responseCircuit.getLong("id"),
                    responseCircuit.getString("name"),
                    getSystemFromJsonObject(responseCircuit.getJSONObject("system"))
                )
                circuitsList.add(circuitObject)
            }
        }
        _circuits.value = circuitsList
    }

    private fun getSystemFromJsonObject(jsonObject: JSONObject): com.matsak.ellicitycompose.dto.System {
        return com.matsak.ellicitycompose.dto.System(
            jsonObject.getLong("id"),
            jsonObject.getString("name"),
            jsonObject.getString("passKey")
        )
    }

    fun errorHandler(response: VolleyError?) {
        println(response)
    }
}
