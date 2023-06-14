package com.matsak.ellicitycompose.screens.systems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.matsak.ellicitycompose.components.CompleteDialogContent
import com.matsak.ellicitycompose.dto.System

var systems: List<com.matsak.ellicitycompose.dto.System> = mutableListOf()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemsContent(
    navController: NavController,
    viewModel: SystemsScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    viewModel.getListOfUserSystems(navController.context)
    val systems by viewModel.systems.observeAsState()

    val systemNameTextInput = rememberSaveable { mutableStateOf("") }
    val systemPassKeyTextInput = rememberSaveable { mutableStateOf("") }

    val addSystemDialogState = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
            if (systems != null) {
                itemsIndexed(
                    systems!!
                ) { _, item ->
                    SystemRow(item) {
                        navController.navigate(SystemScreen.Circuits.name + "/" + item.id)
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(15.dp),
                onClick = { addSystemDialogState.value = true },
                shape = ShapeDefaults.Small,
                colors = CardDefaults.elevatedCardColors(),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(modifier = Modifier
                        .padding(start = 10.dp)
                        .height(20.dp),
                        imageVector = Icons.Default.Add, contentDescription = "Add system")
                    Text(
                        modifier = Modifier.padding(start = 15.dp),
                        text = "Add system",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
        if (addSystemDialogState.value) {
            Dialog(onDismissRequest = { addSystemDialogState.value = false },
                content = {
                    CompleteDialogContent(
                        dialogState = addSystemDialogState,
                        title = "Add new system",
                        successButtonText = "Add",
                        content = { AddSystemBody(systemNameTextInput, systemPassKeyTextInput) },
                        onSuccessButtonClick = {
                            viewModel.addSystemToCurrentUser(
                                navController.context,
                                System(
                                    name = systemNameTextInput.value,
                                    passKey = systemPassKeyTextInput.value
                                )
                            )
                        }
                    )
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSystemBody(
    systemNameTextInput: MutableState<String>,
    systemPassKeyTextInput: MutableState<String>
) {

    Column(modifier = Modifier
        .padding(20.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally)
    {
        OutlinedTextField(
            value = systemNameTextInput.value,
            label = { Text(text = "System name") },
            onValueChange = { systemNameTextInput.value = it })
        OutlinedTextField(
            modifier = Modifier.padding(top = 10.dp),
            label = { Text(text = "Pass Key") },
            value = systemPassKeyTextInput.value,
            onValueChange = { systemPassKeyTextInput.value = it })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemRow(item: System, onItemClick: (System) -> Unit = {}) {
    Card(
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(),
        onClick = { onItemClick(item) },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 10.dp)
    ) {
        Row(Modifier.fillMaxSize()) {
            Icon(
                modifier = Modifier.fillMaxHeight(),
                imageVector = Icons.Default.Place,
                contentDescription = "Icon ${item.id}"
            )
            Text(item.name, Modifier.align(Alignment.CenterVertically))
            Text(
                item.id.toString(),
                Modifier
                    .align(Alignment.CenterVertically),
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun createSystemCard(system: System) {
    return Card(
        onClick = { /* Do something */ },
        modifier = Modifier.size(width = 180.dp, height = 100.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(system.name, Modifier.align(Alignment.Center))
            Text(
                system.id.toString(),
                Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.Gray)
            )
        }
    }
}

enum class SystemScreen {
    Systems,
    Circuits,
    CircuitDetails;

    companion object {
        fun fromRoute(route: String?): SystemScreen = when (route?.substringBefore("/")) {
            Systems.name -> Systems
            Circuits.name -> Circuits
            CircuitDetails.name -> CircuitDetails
            null -> Systems
            else -> throw java.lang.IllegalArgumentException("Route $route is not recognized")
        }
    }
}
