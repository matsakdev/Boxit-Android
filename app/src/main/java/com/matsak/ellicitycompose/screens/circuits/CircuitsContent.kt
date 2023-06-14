package com.matsak.ellicitycompose.screens.circuits

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.*
import com.himanshoe.charty.line.model.LineData
import com.matsak.ellicitycompose.components.LineChart
import com.matsak.ellicitycompose.dto.Circuit
import com.matsak.ellicitycompose.dto.Device
import com.matsak.ellicitycompose.screens.*
import com.matsak.ellicitycompose.screens.systems.SystemScreen
import com.matsak.ellicitycompose.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircuitsContent(
    navController: NavHostController,
    systemId: Long?,
    viewModel: CircuitViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    if (systemId != null) {
        viewModel.getCircuits(navController.context, systemId)
    }
    val circuits by viewModel.circuits.observeAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
            if (circuits != null) {
                itemsIndexed(
                    circuits!!
                ) { _, item ->
                    CircuitRow(item) {
                        navController.navigate(route = SystemScreen.CircuitDetails.name + "/${item.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun CircuitDetails(
    navController: NavHostController, circuitId: Long?,
    viewModel: CircuitDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    if (circuitId != null) {
        viewModel.getMeasurements(navController.context, circuitId)
    }
    val voltageMeasurementData by viewModel.voltage.observeAsState()
    val currentMeasurementData by viewModel.current.observeAsState()
    val devices by viewModel.devices.observeAsState()
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = circuitId.toString(), style = MaterialTheme.typography.headlineLarge)
            //todo remove delete this.let
            voltageMeasurementData?.let {
                LineChart(
                    data = it,
                    colors = listOf(Color.LightGray, Color.Blue, Color.Cyan)
                )
                Spacer(Modifier.padding(top = 10.dp))
                Text(
                    text = "Voltage",
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp
                )
            }
            currentMeasurementData?.let {
                LineChart(
                    data = it,
                    colors = listOf(Color.LightGray, Color.Yellow, Color.Magenta)
                )
                Spacer(Modifier.padding(top = 10.dp))
                Text(
                    text = "Current",
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp
                )
            }
            if (devices != null) {
                DevicesList(devices!!, navController.context, viewModel)
            }
        }
    }
}

@Composable
fun DevicesList(devices: List<Device>, context: Context, viewModel: CircuitDetailsViewModel) {
    LazyColumn(modifier = Modifier.padding(vertical = 10.dp)) {
        itemsIndexed(devices) { _, item -> DeviceCard(item, context, viewModel) }
    }
}

@Composable
fun DeviceCard(device: Device, context: Context, viewModel: CircuitDetailsViewModel) {
    OutlinedCard(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(10.dp)
        .clickable { changeDeviceState(device, context, viewModel) },
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.outlinedCardElevation(),
        shape = ShapeDefaults.Medium,
        colors = if (device.working) CardDefaults.elevatedCardColors()
        else CardDefaults.outlinedCardColors()
    ) {
        Row(modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()) {
            Text(
                textAlign = TextAlign.Start,
                text = device.name,
                softWrap = true,
                color = Color.LightGray
            )
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                textAlign = TextAlign.End,
                text = if (device.working) "ON" else "OFF",
                softWrap = true,
                color = if (device.working) Color.White else Color.DarkGray
            )
        }
    }
}

fun changeDeviceState(device: Device, context: Context, viewModel: CircuitDetailsViewModel) {
    device.working = !device.working
    viewModel.updateDeviceState(context, device)
}

@Composable
@Preview
fun CircuitDetailsPreview() {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "text", style = MaterialTheme.typography.headlineLarge)
            //todo remove delete this.let

            LineChart(
                data = listOf(
                    LineData(1, 2F),
                    LineData(2, 5F),
                    LineData(3, 8F),
                    LineData(4, 22F),
                    LineData(5, 19F),
                ),
                colors = listOf(Color.LightGray, Color.Blue, Color.Cyan)
            )
            Spacer(Modifier.padding(top = 10.dp))
            Text(
                text = "Voltage",
                fontStyle = FontStyle.Normal,
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp
            )
            LineChart(
                data = listOf(
                    LineData(1, 2F),
                    LineData(2, 5F),
                    LineData(3, 8F),
                    LineData(4, 22F),
                    LineData(5, 19F),
                ),
                colors = listOf(Color.LightGray, Color.Yellow, Color.Magenta)
            )
            ActionButton()
        }
    }
}

@Composable
@Preview
fun ActionButton(width: Dp = 100.dp) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .wrapContentSize()
            .width(width),
        colors = CardDefaults.elevatedCardColors(),
        shape = Shapes.small,
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Turn on circuit"
        )
        Text(
            modifier = Modifier.padding(top = 5.dp),
            fontSize = 16.sp,
            text = "Turn on"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircuitRow(item: Circuit, onClick: (Circuit) -> Unit) {
    Card(
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(),
        onClick = { onClick(item) },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Surface(
                modifier = Modifier
                    .padding(15.dp)
                    .size(100.dp),
                shape = RectangleShape,
                tonalElevation = 3.dp,
                shadowElevation = 4.dp
            ) {
                Icon(
                    modifier = Modifier.fillMaxHeight(),
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Icon ${item.id}"
                )
            }
            Text(
                item.name, Modifier.align(Alignment.CenterVertically),
                fontSize = 35.sp
            )
            Text(
                item.id.toString(),
                Modifier
                    .align(Alignment.Top),
                fontStyle = FontStyle.Italic
            )
        }
    }
}