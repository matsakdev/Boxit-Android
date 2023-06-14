package com.matsak.ellicitycompose.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.himanshoe.charty.line.model.LineData
import com.matsak.ellicitycompose.R

@Composable
fun CompleteDialogContent(
    dialogState: MutableState<Boolean>,
    successButtonText: String,
    title: String,
    content: @Composable () -> Unit,
    onSuccessButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxHeight(0.9f)
            .fillMaxWidth(1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TitleAndButton(title, dialogState)
            AddBody(content)
            BottomButtons(successButtonText, dialogState = dialogState, onSuccessButtonClick)
        }
    }
}

@Composable
private fun TitleAndButton(title: String, dialogState: MutableState<Boolean>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 24.sp)
            IconButton(modifier = Modifier.then(Modifier.size(24.dp)),
                onClick = {
                    dialogState.value = false
                }) {
                Icon(
                    Icons.Default.Close,
                    "$title dialog title"
                )
            }
        }
        Divider(color = Color.DarkGray, thickness = 1.dp)
    }
}

@Composable
private fun BottomButtons(
    successButtonText: String,
    dialogState: MutableState<Boolean>,
    onSuccessButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxWidth(1f)
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { dialogState.value = false },
            modifier = Modifier
                .width(120.dp)
                .padding(end = 30.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Cancel", fontSize = 12.sp)
        }
        Button(
            onClick = {
                dialogState.value = false
                onSuccessButtonClick()
            },
            modifier = Modifier.width(120.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = successButtonText, fontSize = 12.sp)
        }

    }
}

@Composable
private fun AddBody(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .padding(10.dp)
    ) {
        content()
    }
}

@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    InputField(
        modifier = modifier,
        valueState = emailState,
        labelId = labelId,
        enabled = enabled,
        imeAction = imeAction,
        keyboardType = KeyboardType.Email,
        onAction = onAction,
        isSingleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        label = { Text(text = labelId) },
        singleLine = isSingleLine,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground),
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        keyboardActions = onAction,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(
    modifier: Modifier,
    passwordState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    passwordVisibility: MutableState<Boolean>,
    onAction: KeyboardActions = KeyboardActions.Default,
    imeAction: ImeAction = ImeAction.Done
) {

    val visualTransformation = if (passwordVisibility.value)
        VisualTransformation.None else PasswordVisualTransformation()
    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = labelId) },
        singleLine = true,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground),
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        trailingIcon = { PasswordVisibility(passwordVisibility = passwordVisibility) },
        keyboardActions = onAction
    )
}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value
    IconButton(onClick = { passwordVisibility.value = !visible }) {
        Icons.Default.Close
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UserForm(
    loading: LiveData<Boolean>,
    isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit,
) {
    val password = rememberSaveable { mutableStateOf("") }
    val repeatedPassword = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = remember { FocusRequester() }
    val repeatedPasswordFocusRequest = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val validate = remember(email.value, password.value, repeatedPassword.value) {
        if (!isCreateAccount) {
            email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
        } else {
            email.value.trim().isNotEmpty()
                    && password.value.trim().isNotEmpty()
                    && password.value.trim() == repeatedPassword.value.trim()
        }
    }

    val modifier = Modifier
        .wrapContentHeight()
        .verticalScroll(rememberScrollState())

    if (isCreateAccount) {
        Text(
            text = stringResource(id = R.string.create_acct),
            modifier = Modifier.padding(4.dp)
        )
    }
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInput(emailState = email, enabled = (!loading.value!!),
            onAction = KeyboardActions {
                passwordFocusRequest.requestFocus()
            })
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelId = "Password",
            enabled = (!loading.value!!),
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                if (isCreateAccount) {
                    repeatedPasswordFocusRequest.requestFocus()
                } else {
                    if (!validate) return@KeyboardActions
                    onDone(email.value.trim(), password.value.trim())
                    keyboardController?.hide()
                }
            },
            imeAction = if (isCreateAccount) ImeAction.Next else ImeAction.Done
        )
        if (isCreateAccount) {
            PasswordInput(modifier = Modifier
                .focusRequester(repeatedPasswordFocusRequest),
                passwordState = repeatedPassword,
                labelId = "Repeat Password",
                enabled = (!loading.value!!),
                passwordVisibility = passwordVisibility,
                onAction = KeyboardActions {
                    if (!validate) return@KeyboardActions
                    onDone(email.value.trim(), password.value.trim())
                    keyboardController?.hide()
                })
        }
        SubmitButton(
            textId = if (isCreateAccount) "Create Account" else "Login",
            loading = loading,
            validInputs = validate
        ) {
            onDone(email.value.trim(), password.value.trim())
            keyboardController?.hide()
        }
    }
}

@Composable
fun SubmitButton(
    textId: String,
    loading: LiveData<Boolean>,
    validInputs: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
        enabled = (!loading.value!!) && validInputs,
        shape = CircleShape
    ) {
        if (loading.value!!) CircularProgressIndicator(modifier = Modifier.size(25.dp))
        else Text(text = textId, modifier = Modifier.padding(5.dp))
    }
}

@Composable
fun LineChart(size: Size = Size(height = 200F, width = 350F),
              data: List<LineData>,
              colors: List<Color>) {
//    val data: List<LineData> = listOf(
//        LineData("14:55", 215.0F),
//        LineData("15:00", 220.3F),
//        LineData("15:05", 225.2F),
//        LineData("15:10", 212.5F),
//        LineData("15:15", 220.2F),
//        LineData("15:20", 222.6F),
//        LineData("14:55", 215.0F),
//        LineData("15:00", 220.3F),
//        LineData("15:05", 225.2F),
//        LineData("15:10", 212.5F),
//        LineData("15:15", 220.2F),
//        LineData("15:20", 222.6F),
//    )
    var brush : Brush = Brush.linearGradient(
        0.0f to Color.LightGray,
        0.3f to Color.Gray,
        1.0f to Color.DarkGray,
        start = Offset(0.0f, 50.0f),
        end = Offset(0.0f, 150.0f)
    )
    Box(modifier = Modifier.wrapContentSize().size(height = size.height.dp, width = size.width.dp)
        .border(width = 2.dp, brush = brush, shape = RectangleShape)
        .padding(20.dp)) {
        com.himanshoe.charty.line.LineChart(
            modifier = Modifier
                .fillMaxSize()
                .height(300.dp)
                .padding(vertical = 10.dp, horizontal = 30.dp),
            colors = colors,
            lineData = data
        )
    }
}
