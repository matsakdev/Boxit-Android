package com.matsak.ellicitycompose.screens;

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.matsak.ellicitycompose.components.UserForm
import com.matsak.ellicitycompose.screens.login.LoginScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    navController: NavHostController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LoginScreenElements(navController, viewModel)
}

@Composable
fun LoginScreenElements(navController: NavHostController, viewModel: LoginScreenViewModel) {
    val showLoginForm = rememberSaveable { mutableStateOf(true) }
    val formIsLoading = viewModel.loading
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            Modifier
                .wrapContentSize()
                .padding(10.dp)
                .background(Color.White),
            shape = ShapeDefaults.Medium
        ) {
            if (showLoginForm.value) {
                UserForm(loading = formIsLoading, isCreateAccount = false) { email, password ->
                    viewModel.signInWithEmailAndPassword(email, password, navController.context) {
                        navController.navigate(EllicityScreens.HomeScreen.name)
                    }
                }
            }
            else {
                UserForm(loading = formIsLoading, isCreateAccount = true) { email, password ->
                    viewModel.createNewUserWithEmailAndPassword(email, password, navController.context) {
                        showLoginForm.value = !showLoginForm.value
                        Toast.makeText(navController.context, "Registration successful! Let's login", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val helpingText = if (showLoginForm.value) "New User?" else "Already have an account?"
            val hrefText = if (showLoginForm.value) "Sign up" else "Login"
            Text(text = helpingText)
            Text(text = hrefText,
                modifier = Modifier
                    .clickable {
                        showLoginForm.value = !showLoginForm.value
                    }
                    .padding(start = 5.dp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}