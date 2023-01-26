package com.chihsuanwu.freescroll

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import detectFreeScrollGestures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Modify element to allow to scroll in both directions.
 *
 * Note that this modifier uses [pointerInput] as the underlying implementation, so some
 * pointer events will be consumed. If you want to use
 * [androidx.compose.foundation.gestures.detectTransformGestures] simultaneously,
 * use [freeScrollWithTransformGesture] instead.
 *
 * @param state state of the scroll
 * @param enabled whether the scroll is enabled
 */
fun Modifier.freeScroll(
    state: FreeScrollState,
    enabled: Boolean = true
): Modifier = composed {

    val velocityTracker = remember { VelocityTracker() }
    val flingSpec = rememberSplineBasedDecay<Float>()

    this.verticalScroll(state = state.verticalScrollState, enabled = false)
        .horizontalScroll(state = state.horizontalScrollState, enabled = false)
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput

            coroutineScope {
                detectDragGestures(
                    onDragStart = { },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(change, dragAmount, state, velocityTracker, this)
                    },
                    onDragEnd = {
                        onEnd(velocityTracker, state, flingSpec, this)
                    }
                )
            }
        }
}

/**
 * Modify element to allow to scroll in both directions, and detect transform gestures.
 * If you don't need to detect transform gestures, use [freeScroll] instead.
 *
 * See [androidx.compose.foundation.gestures.detectTransformGestures] for more details.
 */
fun Modifier.freeScrollWithTransformGesture(
    state: FreeScrollState,
    enabled: Boolean = true,
    panZoomLock: Boolean = true,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit,
): Modifier = composed {

    val velocityTracker = remember { VelocityTracker() }
    val flingSpec = rememberSplineBasedDecay<Float>()

    this.verticalScroll(state = state.verticalScrollState, enabled = false)
        .horizontalScroll(state = state.horizontalScrollState, enabled = false)
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput

            coroutineScope {
                detectFreeScrollGestures(
                    panZoomLock = true,
                    onGesture = { centroid, pan, zoom, rotation, change ->
                        onDrag(change, pan, state, velocityTracker, this)
                        onGesture(centroid, pan, zoom, rotation)
                    },
                    onEnd = {
                        onEnd(velocityTracker, state, flingSpec, this)
                    }
                )
            }
        }
}


@OptIn(ExperimentalComposeUiApi::class)
private fun onDrag(
    change: PointerInputChange,
    dragAmount: Offset,
    state: FreeScrollState,
    velocityTracker: VelocityTracker,
    coroutineScope: CoroutineScope
) {
    // Add historical position to velocity tracker to increase accuracy
    val changeList = change.historical.map {
        it.uptimeMillis to it.position
    } + (change.uptimeMillis to change.position)

    changeList.forEach { (time, pos) ->
        val position = Offset(
            pos.x - state.horizontalScrollState.value,
            pos.y - state.verticalScrollState.value
        )
        velocityTracker.addPosition(time, position)
    }

    coroutineScope.launch {
        state.horizontalScrollState.scrollBy(-dragAmount.x)
        state.verticalScrollState.scrollBy(-dragAmount.y)
    }
}


private fun onEnd(
    velocityTracker: VelocityTracker,
    state: FreeScrollState,
    flingSpec: DecayAnimationSpec<Float>,
    coroutineScope: CoroutineScope
) {
    val velocity = velocityTracker.calculateVelocity()
    velocityTracker.resetTracking()

    // Launch two animation separately to make sure they work simultaneously.
    coroutineScope.launch {
        state.horizontalScrollState.fling(-velocity.x, flingSpec)
    }
    coroutineScope.launch {
        state.verticalScrollState.fling(-velocity.y, flingSpec)
    }
}


/**
 * This is a copy of [androidx.compose.foundation.gestures.DefaultFlingBehavior]
 * with a small modification.
 */
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
