package com.chihsuanwu.freescroll

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
 * @param horizontalReverseScrolling reverse the horizontal scrolling direction,
 * when true, 0 [FreeScrollState.xValue] will mean right, otherwise left.
 * @param verticalReverseScrolling reverse the vertical scrolling direction,
 * when true, 0 [FreeScrollState.yValue] will mean bottom, otherwise top.
 * @param flingBehavior logic describing fling behavior when drag has finished with velocity.
 * If null, default from [ScrollableDefaults.flingBehavior] will be used.
 */
fun Modifier.freeScroll(
    state: FreeScrollState,
    enabled: Boolean = true,
    horizontalReverseScrolling: Boolean = false,
    verticalReverseScrolling: Boolean = false,
    flingBehavior: FlingBehavior? = null,
): Modifier = composed {

    val velocityTracker = remember { VelocityTracker() }
    val fling = flingBehavior ?: ScrollableDefaults.flingBehavior()

    this.horizontalScroll(
        state = state.horizontalScrollState,
        enabled = false,
        reverseScrolling = horizontalReverseScrolling
    ).verticalScroll(
        state = state.verticalScrollState,
        enabled = false,
        reverseScrolling = verticalReverseScrolling
    )
    .pointerInput(enabled, horizontalReverseScrolling, verticalReverseScrolling) {
        if (!enabled) return@pointerInput

        coroutineScope {
            detectDragGestures(
                onDragStart = { },
                onDrag = { change, dragAmount ->
                    change.consume()
                    onDrag(
                        change = change,
                        dragAmount = dragAmount,
                        state = state,
                        horizontalReverseScrolling = horizontalReverseScrolling,
                        verticalReverseScrolling = verticalReverseScrolling,
                        velocityTracker = velocityTracker,
                        coroutineScope = this
                    )
                },
                onDragEnd = {
                    onEnd(
                        velocityTracker = velocityTracker,
                        state = state,
                        horizontalReverseScrolling = horizontalReverseScrolling,
                        verticalReverseScrolling = verticalReverseScrolling,
                        flingBehavior = fling,
                        coroutineScope = this
                    )
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
    panZoomLock: Boolean = false,
    horizontalReverseScrolling: Boolean = false,
    verticalReverseScrolling: Boolean = false,
    flingBehavior: FlingBehavior? = null,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit,
): Modifier = composed {

    val velocityTracker = remember { VelocityTracker() }
    val fling = flingBehavior ?: ScrollableDefaults.flingBehavior()

    this.horizontalScroll(
        state = state.horizontalScrollState,
        enabled = false,
        reverseScrolling = horizontalReverseScrolling
    ).verticalScroll(
        state = state.verticalScrollState,
        enabled = false,
        reverseScrolling = verticalReverseScrolling
    )
    .pointerInput(enabled, horizontalReverseScrolling, verticalReverseScrolling) {
        if (!enabled) return@pointerInput

        coroutineScope {
            detectFreeScrollGestures(
                panZoomLock = panZoomLock,
                onGesture = { centroid, pan, zoom, rotation, change ->
                    onDrag(
                        change = change,
                        dragAmount = pan,
                        state = state,
                        horizontalReverseScrolling = horizontalReverseScrolling,
                        verticalReverseScrolling = verticalReverseScrolling,
                        velocityTracker = velocityTracker,
                        coroutineScope = this
                    )
                    onGesture(centroid, pan, zoom, rotation)
                },
                onEnd = {
                    onEnd(
                        velocityTracker = velocityTracker,
                        state = state,
                        horizontalReverseScrolling = horizontalReverseScrolling,
                        verticalReverseScrolling = verticalReverseScrolling,
                        flingBehavior = fling,
                        coroutineScope = this
                    )
                }
            )
        }
    }
}


/**
 * If [change] is null, it means that the id of the pointer is changed. This happens when
 * freeScrollWithTransformGesture is used. In this case, we need to reset the velocity tracker to
 * avoid incorrect velocity calculation.
 */
@OptIn(ExperimentalComposeUiApi::class)
private fun onDrag(
    change: PointerInputChange?,
    dragAmount: Offset,
    state: FreeScrollState,
    horizontalReverseScrolling: Boolean,
    verticalReverseScrolling: Boolean,
    velocityTracker: VelocityTracker,
    coroutineScope: CoroutineScope,
) {

    coroutineScope.launch {
        state.horizontalScrollState.scrollBy(
            if (horizontalReverseScrolling) dragAmount.x else -dragAmount.x
        )
        state.verticalScrollState.scrollBy(
            if (verticalReverseScrolling) dragAmount.y else -dragAmount.y
        )
    }

    if (change == null) {
        velocityTracker.resetTracking()
        return
    }

    // Add historical position to velocity tracker to increase accuracy
    val changeList = change.historical.map {
        it.uptimeMillis to it.position
    } + (change.uptimeMillis to change.position)

    changeList.forEach { (time, pos) ->
        val position = Offset(
            x = pos.x - if (horizontalReverseScrolling)
                -state.horizontalScrollState.value
            else
                state.horizontalScrollState.value,
            y = pos.y - if (verticalReverseScrolling)
                -state.verticalScrollState.value
            else
                state.verticalScrollState.value,
        )
        velocityTracker.addPosition(time, position)
    }
}


private fun onEnd(
    velocityTracker: VelocityTracker,
    state: FreeScrollState,
    horizontalReverseScrolling: Boolean,
    verticalReverseScrolling: Boolean,
    flingBehavior: FlingBehavior,
    coroutineScope: CoroutineScope
) {
    val velocity = velocityTracker.calculateVelocity()
    velocityTracker.resetTracking()

    // Launch two animation separately to make sure they work simultaneously.
    coroutineScope.launch {
        state.horizontalScrollState.scroll {
            with(flingBehavior) {
                performFling(if (horizontalReverseScrolling) velocity.x else -velocity.x)
            }
        }
    }
    coroutineScope.launch {
        state.verticalScrollState.scroll {
            with(flingBehavior) {
                performFling(if (verticalReverseScrolling) velocity.y else -velocity.y)
            }
        }
    }
}
