package com.matsak.ellicitycompose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.matsak.ellicitycompose.graphs.RootNavigationGraph
import com.matsak.ellicitycompose.ui.theme.EllicityComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApplication {
                RootNavigationGraph(navController = rememberNavController())
            }
        }
    }
}

@Composable
fun MainApplication(content: @Composable () -> Unit) {
    EllicityComposeTheme {
        content()
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)

@Composable
fun PreviewMessageCard() {
    EllicityComposeTheme {
        RootNavigationGraph(navController = rememberNavController())
    }
}