package com.matsak.ellicitycompose.screens.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.matsak.ellicitycompose.R
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginScreenViewModel : ViewModel() {
    //    val loadingState = MutableStateFlow(LoadingState.Status.IDLE)
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        ctx: Context,
        home: () -> Unit
    ) = viewModelScope.launch {
        try {
            val queue = Volley.newRequestQueue(ctx) //todo problem here
            val urlBase = ctx.getString(R.string.baseURL)
            val loginPath = ctx.getString(R.string.login)
            val url = urlBase + loginPath

            val params: MutableMap<String, String> = mutableMapOf<String, String>()
            params["email"] = email
            params["password"] = password

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, JSONObject(params as Map<*, *>),
                { successResponse ->
                    successfulLoginRequest(successResponse, ctx, home);
                },
                { errorResponse ->
                    errorHandler(errorResponse, ctx);
                })

            queue.add(jsonObjectRequest)
        } catch (ex: java.lang.Exception) {
            println("Login error: ${ex.message}")
        }
    }

    fun createNewUserWithEmailAndPassword(
        email: String,
        password: String,
        ctx: Context,
        action: () -> Unit
    ) {
        try {
            if (_loading.value == false) {
                _loading.value = true
                val queue = Volley.newRequestQueue(ctx) //todo problem here
                val urlBase = ctx.getString(R.string.baseURL)
                val signup = ctx.getString(R.string.signup)
                val url = urlBase + signup

                val params: MutableMap<String, String> = mutableMapOf<String, String>()
                params["email"] = email
                params["name"] = "Guest"
                params["password"] = password

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url, JSONObject(params as Map<*, *>),
                    { successResponse ->
                        successfulRegistrationRequest(successResponse, ctx, action)
                        _loading.value = false
                    },
                    { errorResponse ->
                        errorHandler(errorResponse, ctx)
                        _loading.value = false
                    })

                queue.add(jsonObjectRequest)
            }
        } catch (ex: java.lang.Exception) {
            println("Login error: ${ex.message}")
        }
    }

    private fun successfulLoginRequest(response: JSONObject?, ctx: Context, home: () -> Unit) {
        response?.let {
            if (response.has("accessToken")) {
                val authResponse: AuthResponse =
                    GsonBuilder().create().fromJson(response.toString(), AuthResponse::class.java)
                ctx
                    .getSharedPreferences("com.matsak.ellicitycompose", Context.MODE_PRIVATE).edit()
                    .putString("token", authResponse.accessToken)
                    .putString("tokenType", authResponse.tokenType)
                    .apply()
                home()
            } else {
                println("LOGIN ERROR AFTER SUCCESS RESPONSE")
            }
        }
    }

    private fun successfulRegistrationRequest(
        response: JSONObject?,
        ctx: Context,
        action: () -> Unit
    ) {
        response?.let {
            if (response.has("success") && response.getBoolean("success")) {
                action()
            } else {
                Toast.makeText(ctx, "REGISTRATION ERROR AFTER RESPONSE: $response", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun errorHandler(response: VolleyError?, ctx: Context) {
        Toast.makeText(ctx, "Login error. Check your email and password", Toast.LENGTH_LONG).show()
    }

    data class AuthResponse(val accessToken: String, val tokenType: String)
}