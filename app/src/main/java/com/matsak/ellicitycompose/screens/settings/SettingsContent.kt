package com.matsak.ellicitycompose.screens.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.matsak.ellicitycompose.components.CompleteDialogContent
import com.matsak.ellicitycompose.service.InfoUpdateService

@Composable
fun SettingsContent(
    navController: NavController,
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            viewModel.getUserInfo(navController.context)
            val userInfo = viewModel.user.observeAsState()
            val notificationsChecked = rememberSaveable { mutableStateOf(false) }
            val changeNameDialogState = rememberSaveable { mutableStateOf(false) }
            val changeEmailDialogState = rememberSaveable { mutableStateOf(false) }
            val changeNameTextInput = rememberSaveable { mutableStateOf("") }
            val changeEmailTextInput = rememberSaveable { mutableStateOf("") }
            if (userInfo.value != null) {
                Text(modifier = Modifier.padding(15.dp), text = userInfo.value!!.name)
                Text(modifier = Modifier.padding(15.dp), text = userInfo.value!!.email)
            }
            SettingsRadioButtonItem(
                title = "Notification service working",
                isChecked = notificationsChecked,
                ctx = navController.context
            )
            SettingsDialogItem(
                dialogState = changeNameDialogState,
                dialogTitle = "Change username",
                rowTitle = "Username: " + if (userInfo.value != null) userInfo.value!!.name else "Guest",
                successButtonText = "Change",
                content = {
                    SettingsAddDialogBody(
                        labelId = "New name",
                        textInput = changeNameTextInput
                    )
                }) {
                viewModel.changeName(changeNameTextInput.value, ctx = navController.context)
            }
            SettingsDialogItem(
                dialogState = changeEmailDialogState,
                dialogTitle = "Change email",
                rowTitle = "Email: " + if (userInfo.value != null) userInfo.value!!.email else "no set",
                successButtonText = "Change",
                content = {
                    SettingsAddDialogBody(
                        labelId = "New email",
                        textInput = changeEmailTextInput
                    )
                }) {
                viewModel.changeEmail(
                    changeEmailTextInput.value,
                    ctx = navController.context)
            }
        }
    }
}

@Composable
fun SettingsRadioButtonItem(
    title: String,
    isChecked: MutableState<Boolean>,
    ctx: Context
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .background(color = Color.Gray, shape = ShapeDefaults.ExtraSmall)
    ) {
        Text(
            text = title,
            Modifier
                .align(Alignment.CenterStart)
                .padding(10.dp),
            fontSize = 20.sp
        )
        Switch(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .semantics { contentDescription = title }
                .padding(10.dp),
            checked = isChecked.value,
            onCheckedChange = {
                isChecked.value = !isChecked.value
                if (isChecked.value) {
                    ctx.startService(Intent(ctx, InfoUpdateService::class.java))
                } else {
                    ctx.stopService(Intent(ctx, InfoUpdateService::class.java))
                }
            })
    }
}

@Composable
fun SettingsDialogItem(
    rowTitle: String,
    dialogTitle: String,
    successButtonText: String,
    dialogState: MutableState<Boolean>,
    content: @Composable () -> Unit,
    onSuccess: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .background(color = Color.Gray, shape = ShapeDefaults.ExtraSmall)
    ) {
        Text(
            text = rowTitle,
            Modifier
                .align(Alignment.CenterStart)
                .padding(10.dp),
            fontSize = 20.sp
        )
        Button(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterEnd)
                .padding(10.dp),
            shape = ShapeDefaults.Small,
            onClick = { dialogState.value = !dialogState.value }
        ) {
            Text(
                modifier = Modifier
                    .padding(5.dp),
                fontSize = 15.sp,
                text = successButtonText
            )
        }
        if (dialogState.value) {
            Dialog(onDismissRequest = { dialogState.value = false },
                content = {
                    CompleteDialogContent(
                        dialogState = dialogState,
                        title = dialogTitle,
                        successButtonText = successButtonText,
                        content = content,
                        onSuccessButtonClick = onSuccess
                    )
                })
        }
    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsAddDialogBody(
        labelId: String,
        textInput: MutableState<String>,
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            OutlinedTextField(
                value = textInput.value,
                label = { Text(text = labelId) },
                onValueChange = { textInput.value = it })
        }
    }

    @Composable
    @Preview
    fun SettingsContentPreview() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                itemsIndexed(
                    listOf("Service works")
                ) { _, item ->
                    var checked by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp)
                            .background(color = Color.Gray, shape = ShapeDefaults.ExtraSmall)
                    ) {
                        Text(
                            text = item,
                            Modifier
                                .align(Alignment.CenterStart)
                                .padding(10.dp),
                            fontSize = 20.sp
                        )
                        Switch(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .semantics { contentDescription = item }
                                .padding(10.dp),
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                            })
                    }
                }
            }
        }

    }