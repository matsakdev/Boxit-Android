package com.matsak.ellicitycompose.service

import android.app.PendingIntent
import com.matsak.ellicitycompose.dto.SystemsList
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.matsak.ellicitycompose.R
import org.json.JSONArray
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class InfoUpdateService : Service() {

    private lateinit var es: ExecutorService
    lateinit var requestProcessor : Requester

    override fun onCreate() {
        println("create service")
        es = Executors.newFixedThreadPool(3);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("start command")
        var tokenType : String? = intent?.getStringExtra("tokenType")
        var token : String? = intent?.getStringExtra("token")
        var pi: PendingIntent? =
                intent?.getParcelableExtra(getString(R.string.SYSTEMS_PINTENT))

        requestProcessor = Requester(token, tokenType, startId, pi, this)
        es.execute(requestProcessor)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        println("destroy")
        requestProcessor.stopExecution()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        return SystemsBind()
    }

    class SystemsBind : Binder() {

    }

    class Requester(
        var token: String?,
        var tokenType: String?,
        var startId: Int,
        var pi: PendingIntent?,
        var ctx: Context
        ) : Runnable {
        private var isWorking: Boolean = true
        var currentList : List<com.matsak.ellicitycompose.dto.System> = listOf()
        override fun run() {
            try {
                while (isWorking) {
                    updateSystemsList(currentList, token, tokenType, pi, ctx)
                    println("working task")
                    Thread.sleep(2000)
                }
                return
            }
            catch (e : java.lang.Exception) {
                println(e)
            }
        }

        fun stopExecution() {
            isWorking = false
        }

        private fun updateSystemsList(
            oldList: List<com.matsak.ellicitycompose.dto.System>,
            token: String?,
            tokenType: String?,
            pi: PendingIntent?,
            ctx: Context
        ) {
            val listSnapshot = listOf(oldList)
            getSystemsArray(token, tokenType, ctx)
            println("CURRENT LIST EQUALS OLD? : " + (currentList == listSnapshot))
            println("current list: $currentList\nold list: $listSnapshot")
            if (currentList != listSnapshot) {
                var intent : Intent = Intent().putExtra("systems", SystemsList(currentList));
                pi?.send(this.ctx, ctx.getString(R.string.STATUS_FINISH).toInt(), intent)
            }
        }

        fun getSystemsArray(token: String?, tokenType: String?, ctx: Context) {
            val queue = Volley.newRequestQueue(ctx)
            val url = ctx.getString(R.string.baseURL) + ctx.getString(R.string.systemsURL)

            val request = object: JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener<JSONArray> { response ->
                    successfulRequest(response)
                },
                Response.ErrorListener { response ->
                    errorHandler(response);
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    val token : String? = token
                    val tokenType : String? = tokenType
                    headers["Authorization"] = "$tokenType $token"
                    return headers
                }
            }

            queue.add(request)
        }

        fun successfulRequest(response: JSONArray){
            val systems: ArrayList<com.matsak.ellicitycompose.dto.System> = arrayListOf()
            response.let{
                for (i in 0 until response.length()) {
                    val responseSystem= response.getJSONObject(i);
                    var systemObject : com.matsak.ellicitycompose.dto.System
                    systemObject = com.matsak.ellicitycompose.dto.System(responseSystem.getLong("id"),
                        responseSystem.getString("name"), responseSystem.getString("passKey"))
                    systems.add(systemObject)
                }
            }
            changeCurrentList(systems)
        }

        private fun changeCurrentList(systems: ArrayList<com.matsak.ellicitycompose.dto.System>) {
            currentList = systems.toList()
        }

        data class AuthResponse(val accessToken: String, val tokenType: String)

        fun errorHandler(response: VolleyError?) {
            println(response)
        }
    }
}