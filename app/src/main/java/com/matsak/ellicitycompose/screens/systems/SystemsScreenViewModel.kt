package com.matsak.ellicitycompose.screens.systems

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.matsak.ellicitycompose.R
import com.matsak.ellicitycompose.utils.HttpRequester
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SystemsScreenViewModel : ViewModel() {

    private val _systems = MutableLiveData<List<com.matsak.ellicitycompose.dto.System>>()
    val systems = _systems

    fun getListOfUserSystems(ctx: Context) {
        val executor: ExecutorService = Executors.newSingleThreadExecutor();
        executor.execute {
            sendingRequests = true
            while (sendingRequests) {
                sendRequest(ctx)
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

    private fun sendRequest(ctx: Context) {
        val queue = Volley.newRequestQueue(ctx)
        val url = ctx.getString(R.string.baseURL) + ctx.getString(R.string.systemsURL)

        val request = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONArray> { response ->
                successfulRequest(response)
            },
            Response.ErrorListener { response ->
                errorHandler(response);
            }) {
            override fun getHeaders(): MutableMap<String, String> {
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
        }
        queue.add(request)
    }

    fun successfulRequest(response: JSONArray) {
        val systemsList: ArrayList<com.matsak.ellicitycompose.dto.System> = arrayListOf()
        response.let {
            for (i in 0 until response.length()) {
                val responseSystem = response.getJSONObject(i);
                val systemObject = com.matsak.ellicitycompose.dto.System(
                    responseSystem.getLong("id"),
                    responseSystem.getString("name"), responseSystem.getString("passKey")
                )
                systemsList.add(systemObject)
            }
        }
        _systems.value = systemsList
    }


    fun errorHandler(response: VolleyError?) {
        println(response)
    }

    fun addSystemToCurrentUser(ctx: Context, system: com.matsak.ellicitycompose.dto.System) {
        HttpRequester.sendPostRequestWithBodyAndProcessResult(
            ctx = ctx,
            url = ctx.getString(R.string.baseURL) + ctx.getString(R.string.systemsURL) + ctx.getString(
                R.string.connectToSystemURL
            ),
            bodyParams = mutableMapOf(
                Pair("systemName", system.name),
                Pair("passKey", system.passKey)
            ),
            onSuccess = { response : JSONObject ->
                if (response.has("success") && response.getBoolean("success")) {
                    println("\n\n $response \n\n")
                    Toast.makeText(
                        ctx,
                        "Hey! You've added the system successfully",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (response.has("success") && response.getBoolean("success")) {
                    Toast.makeText(ctx, "The error has been occurred", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(ctx, response.getString("message"), Toast.LENGTH_LONG).show()
                }
            },
            onError = { response -> HttpRequester.defaultOnError(response, ctx) }
        )
    }

}