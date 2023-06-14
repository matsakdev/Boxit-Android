package com.matsak.ellicitycompose.screens.settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.matsak.ellicitycompose.R
import com.matsak.ellicitycompose.dto.UserDto
import com.matsak.ellicitycompose.utils.HttpRequester

class SettingsViewModel : ViewModel() {

    private var _user = MutableLiveData<UserDto>()
    public val user = _user

    fun getUserInfo(ctx: Context) {
        HttpRequester.sendGetRequestWithEmptyBodyAndProcessJsonObject(
            ctx = ctx,
            url = ctx.getString(R.string.baseURL) + ctx.getString(R.string.userURL) + "/me",
            onSuccess = {
                var userDto = GsonBuilder().create().fromJson(it.toString(), UserDto::class.java)
                updateUserInfo(userDto)
            },
            onError = { error -> HttpRequester.defaultOnError(error, ctx) }
        )
    }

    private fun updateUserInfo(userDto: UserDto?) {
        if (userDto != null) {
            _user.value = userDto
        } else {
            throw java.lang.IllegalStateException("User is null")
        }
    }

    fun changeName(
        newName: String,
        ctx: Context
    ) {
        changeUserInfo(ctx, UserDto(_user.value!!.id, newName, _user.value!!.email))
    }

    fun changeEmail(
        newEmail: String,
        ctx: Context
    ) {
        changeUserInfo(ctx, UserDto(_user.value!!.id, _user.value!!.name, newEmail))
    }

    private fun changeUserInfo(ctx: Context, userDto: UserDto) {
        HttpRequester.sendPutRequestWithBodyAndProcessResult(
            ctx = ctx,
            url = ctx.getString(R.string.baseURL) + ctx.getString(R.string.userURL) + "/" + _user.value?.id,
            bodyParams = mutableMapOf(
                Pair("id", userDto.id.toString()),
                Pair("name", userDto.name),
                Pair("email", userDto.email),
            ),
            onSuccess = { response ->
                if (response.has("success") && response.getBoolean("success")) {
                    _user.value = userDto
                } else if (response.has("success")) {
                    Toast.makeText(ctx, "The error has been occurred", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(ctx, response.getString("message"), Toast.LENGTH_LONG).show()
                }
            },
            onError = { error -> HttpRequester.defaultOnError(error, ctx) }
        )
    }

}
