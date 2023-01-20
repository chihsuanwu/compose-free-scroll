package com.chihsuanwu.freescroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chihsuanwu.freescroll.ui.theme.ComposefreescrollTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposefreescrollTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FreeScrollView(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    )
                }
            }
        }
    }
}


@Composable
private fun OfficialScrollView(modifier: Modifier = Modifier) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    Column(modifier = modifier
        .horizontalScroll(horizontalScrollState)
        .verticalScroll(verticalScrollState)
    ) {
        Content()
    }
}

@Composable
private fun FreeScrollView(modifier: Modifier = Modifier) {
    val state = rememberFreeScrollState()
    var enable by remember {
        mutableStateOf(true)
    }
    Column(
        modifier = modifier
            .freeScroll(
                state = state,
                enabled = enable
            )
    ) {
        Button(onClick = {
             enable = !enable
        }) {
            Text(text = "Enable: $enable")
        }
        Content()
    }
}

@Composable
private fun Content() {
    (0..20).forEach { r ->
        Row {
            (0..10).forEach { c ->
                Text(
                    text = "Hello $r-$c",
                    modifier = Modifier
                        .size(120.dp, 80.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }
    }
}


@Composable
@Preview
fun Preview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        OfficialScrollView(
            modifier = Modifier
                .weight(1f)
                .background(Color.Gray)
        )
        FreeScrollView(
            modifier = Modifier
                .weight(1f)
                .background(Color.LightGray)
        )
    }
}