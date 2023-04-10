package com.chihsuanwu.freescroll

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chihsuanwu.freescroll.ui.theme.ComposefreescrollTheme
import kotlinx.coroutines.launch

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
                    )
                }
            }
        }
    }
}

@Composable
private fun FreeScrollView(modifier: Modifier = Modifier) {
    val state = rememberFreeScrollState(400, 400)
    val coroutineScope = rememberCoroutineScope()
    var enable by remember { mutableStateOf(true) }
    var horizontalReverse by remember { mutableStateOf(false) }
    var verticalReverse by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .background(Color.Green)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Text(text = "x: ${state.xValue}, y: ${state.yValue}, max x: ${state.xMaxValue}, max y: ${state.yMaxValue}")

            Button(onClick = {
                enable = !enable
            }) {
                Text(text = "Enable: $enable")
            }
            Button(onClick = {
                horizontalReverse = !horizontalReverse
            }) {
                Text(text = "Horizontal reverse: $horizontalReverse")
            }
            Button(onClick = {
                verticalReverse = !verticalReverse
            }) {
                Text(text = "Vertical reverse: $verticalReverse")
            }
        }
        Row(
            modifier = Modifier
                .background(Color.Green)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Button(onClick = {
                coroutineScope.launch {
                    state.scrollTo(100, 200)
                }
            }) {
                Text(text = "Scroll to 100, 200")
            }

            Button(onClick = {
                coroutineScope.launch {
                    state.scrollBy(Offset(100f, 200f))
                }
            }) {
                Text(text = "Scroll by 100, 200")
            }

            Button(onClick = {
                coroutineScope.launch {
                    state.animateScrollTo(200, 400)
                }
            }) {
                Text(text = "Animation scroll to 200, 400")
            }

            Button(onClick = {
                coroutineScope.launch {
                    state.animateScrollBy(Offset(100f, 200f))
                }
            }) {
                Text(text = "Animation scroll by 100, 200")
            }
        }

        Column(
            modifier = modifier
                .weight(1f)
                .freeScrollWithTransformGesture(
                    state = state,
                    enabled = enable,
                    horizontalReverseScrolling = horizontalReverse,
                    verticalReverseScrolling = verticalReverse,
                    onGesture = { _, _, zoom, _ ->
                        Log.d("FreeScrollView", "onGesture: $zoom")
                    }
                )
        ) {
            Content()
        }
    }
}

@Composable
private fun Content() {
    (0..20).forEach { r ->
        Row {
            (0..10).forEach { c ->
                Box(
                    modifier = Modifier
                        .size(120.dp, 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hello $r-$c",
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun Preview() {
    FreeScrollView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    )
}