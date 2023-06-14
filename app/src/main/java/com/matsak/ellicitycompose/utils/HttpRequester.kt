package com.matsak.ellicitycompose.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.matsak.ellicitycompose.R
import org.json.JSONArray
import org.json.JSONObject

class HttpRequester {
    companion object {

        fun sendGetRequestWithEmptyBodyAndProcessJsonArray(
            ctx: Context,
            url: String,
            onSuccess: (JSONArray) -> Unit,
            onError: (VolleyError) -> Unit,
            headers: MutableMap<String, String> = mutableMapOf()
        ) {
            sendRequestAndProcessJsonArray(
                ctx = ctx,
                url = url,
                method = Request.Method.GET,
                onSuccess = onSuccess,
                onError = onError,
                headers = if (headers.isEmpty()) getAuthorizationHeaders(ctx) else headers
            )
        }

        fun sendGetRequestWithEmptyBodyAndProcessJsonObject(
            ctx: Context,
            url: String,
            onSuccess: (JSONObject) -> Unit,
            onError: (VolleyError) -> Unit,
            headers: MutableMap<String, String> = mutableMapOf()
        ) {
            sendRequestAndProcessJsonObject(
                ctx = ctx,
                url = url,
                method = Request.Method.GET,
                onSuccess = onSuccess,
                onError = onError,
                headers = if (headers.isEmpty()) getAuthorizationHeaders(ctx) else headers
            )
        }

        fun sendPostRequestWithBodyAndProcessResult(
            ctx: Context,
            url: String,
            onSuccess: (JSONObject) -> Unit,
            onError: (VolleyError) -> Unit,
            bodyParams: MutableMap<String, String>,
            headers: MutableMap<String, String> = mutableMapOf()
        ) {
            sendRequestAndProcessJsonObject(
                ctx = ctx,
                url = url,
                method = Request.Method.POST,
                onSuccess = onSuccess,
                onError = onError,
                bodyParams = bodyParams,
                headers = if (headers.isEmpty()) getAuthorizationHeaders(ctx) else headers
            )
        }

        fun sendPutRequestWithBodyAndProcessResult(
            ctx: Context,
            url: String,
            onSuccess: (JSONObject) -> Unit,
            onError: (VolleyError) -> Unit,
            bodyParams: MutableMap<String, String>,
            headers: MutableMap<String, String> = mutableMapOf()
        ) {
            sendRequestAndProcessJsonObject(
                ctx = ctx,
                url = url,
                method = Request.Method.PUT,
                onSuccess = onSuccess,
                onError = onError,
                bodyParams = bodyParams,
                headers = if (headers.isEmpty()) getAuthorizationHeaders(ctx) else headers
            )
        }


        private fun sendRequestAndProcessJsonArray(
            ctx: Context,
            url: String,
            method: Int,
            bodyParams: MutableMap<String, String> = mutableMapOf(),
            onSuccess: (JSONArray) -> Unit,
            headers: MutableMap<String, String>,
            onError: (VolleyError) -> Unit
        ) {
            val queue = Volley.newRequestQueue(ctx)

            val jsonRequestBody = if (bodyParams.isNotEmpty()) JSONArray(bodyParams as Map<*, *>)
            else null

            val request = object : JsonArrayRequest(
                method, url, jsonRequestBody,
                Response.Listener<JSONArray> { response ->
                    onSuccess(response)
                },
                Response.ErrorListener { response ->
                    onError(response);
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return headers
                }
            }

            queue.add(request)
        }

        private fun sendRequestAndProcessJsonObject(
            ctx: Context,
            url: String,
            method: Int,
            bodyParams: MutableMap<String, String> = mutableMapOf(),
            headers: MutableMap<String, String>,
            onSuccess: (JSONObject) -> Unit,
            onError: (VolleyError) -> Unit
        ) {
            val queue = Volley.newRequestQueue(ctx)

            val jsonRequestBody = if (bodyParams.isNotEmpty()) JSONObject(bodyParams as Map<*, *>)
            else null

            val request = object : JsonObjectRequest(
                method, url, jsonRequestBody,
                Response.Listener<JSONObject> { response ->
                    onSuccess(response)
                },
                Response.ErrorListener { response ->
                    onError(response)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return headers
                }
            }

            queue.add(request)
        }

        fun defaultOnError(error: VolleyError, ctx: Context) {
            Toast.makeText(ctx, error.toString(), Toast.LENGTH_LONG).show();
            println(error.networkResponse.data)
        }

        fun getAuthorizationHeaders(ctx: Context): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            val token: String? =
                ctx.getSharedPreferences("com.matsak.ellicitycompose", Context.MODE_PRIVATE)
                    .getString("token", "");
            val tokenType: String? =
                ctx.getSharedPreferences("com.matsak.ellicitycompose", Context.MODE_PRIVATE)
                    .getString("tokenType", "");
            headers["Authorization"] = "$tokenType $token"
            headers["Content-Type"] = "application/json"
            return headers
        }

        fun sendPostRequestWithoutBodyAndProcessResult(
            ctx: Context,
            url: String,
            onSuccess: (JSONObject) -> Unit,
            onError: (VolleyError) -> Unit,
            headers: MutableMap<String, String> = mutableMapOf()
        ) {
            sendRequestAndProcessJsonObject(
                ctx = ctx,
                url = url,
                method = Request.Method.POST,
                onSuccess = onSuccess,
                onError = onError,
                headers = if (headers.isEmpty()) getAuthorizationHeaders(ctx) else headers
            )
        }


    }
}