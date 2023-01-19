package com.chihsuanwu.freescroll

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.freeScroll(): Modifier = composed {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()

    val velocityTracker = remember {
        VelocityTracker()
    }

    val flingSpec = rememberSplineBasedDecay<Float>()

    this.verticalScroll(state = verticalScrollState, enabled = false)
        .horizontalScroll(state = horizontalScrollState, enabled = false)
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { },
                onDrag = { change, dragAmount ->
                    change.consume()

                    // Add historical position to velocity tracker to increase accuracy
                    val changeList = change.historical.map {
                        it.uptimeMillis to it.position
                    } + (change.uptimeMillis to change.position)

                    changeList.forEach { (time, pos) ->
                        val position = Offset(
                            pos.x - horizontalScrollState.value,
                            pos.y - verticalScrollState.value
                        )
                        velocityTracker.addPosition(time, position)
                    }

                    coroutineScope.launch {
                        horizontalScrollState.scrollBy(-dragAmount.x)
                        verticalScrollState.scrollBy(-dragAmount.y)
                    }
                },
                onDragEnd = {
                    val velocity = velocityTracker.calculateVelocity()
                    velocityTracker.resetTracking()

                    // Launch two animation separately to make sure they work simultaneously.
                    coroutineScope.launch {
                        horizontalScrollState.fling(-velocity.x, flingSpec)
                    }
                    coroutineScope.launch {
                        verticalScrollState.fling(-velocity.y, flingSpec)
                    }
                }
            )
        }
}

private suspend fun ScrollState.fling(initialVelocity: Float, flingDecay: DecayAnimationSpec<Float>) {
    if (abs(initialVelocity) < 0.1f) return // Ignore flings with very low velocity

    scroll {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = initialVelocity,
        ).animateDecay(flingDecay) {
            val delta = value - lastValue
            val consumed = scrollBy(delta)
            lastValue = value
            // avoid rounding errors and stop if anything is unconsumed
            if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
        }
    }
}
