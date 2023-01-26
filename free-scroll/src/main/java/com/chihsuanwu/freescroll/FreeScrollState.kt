package com.chihsuanwu.freescroll

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * State of the scroll. Allows to control the scroll position and to observe the scroll position.
 *
 * To create an automatically remembered [FreeScrollState], use [rememberFreeScrollState].
 */
class FreeScrollState(
    val horizontalScrollState: ScrollState,
    val verticalScrollState: ScrollState,
) {

    /**
     * current horizontal scroll position value in pixels
     */
    val xValue: Int get() = horizontalScrollState.value

    /**
     * current vertical scroll position value in pixels
     */
    val yValue: Int get() = verticalScrollState.value


    /**
     * maximum bound for [xValue], or [Int.MAX_VALUE] if still unknown
     */
    val xMaxValue: Int get() = horizontalScrollState.maxValue

    /**
     * maximum bound for [yValue], or [Int.MAX_VALUE] if still unknown
     */
    val yMaxValue: Int get() = verticalScrollState.maxValue


    /**
     * Jump instantly by [offset] pixels.
     *
     * @see animateScrollBy for an animated version
     *
     * @param offset number of pixels to scroll by
     * @return the amount of scroll consumed
     */
    suspend fun scrollBy(
        offset: Offset
    ): Offset = coroutineScope {
        val xOffset = async {
            horizontalScrollState.scrollBy(offset.x)
        }
        val yOffset = async {
            verticalScrollState.scrollBy(offset.y)
        }
        Offset(xOffset.await(), yOffset.await())
    }

    /**
     * Instantly jump to the given position in pixels.
     *
     * @see animateScrollTo for an animated version
     *
     * @param x the horizontal position to scroll to
     * @param y the vertical position to scroll to
     * @return the amount of scroll consumed
     */
    suspend fun scrollTo(
        x: Int,
        y: Int,
    ): Offset = coroutineScope {
        val xOffset = async {
            horizontalScrollState.scrollTo(x)
        }
        val yOffset = async {
            verticalScrollState.scrollTo(y)
        }
        Offset(xOffset.await(), yOffset.await())
    }

    /**
     * Scroll by [offset] pixels with animation.
     *
     * @param offset number of pixels to scroll by
     * @param animationSpec [AnimationSpec] to be used for this scrolling
     *
     * @return the amount of scroll consumed
     */
    suspend fun animateScrollBy(
        offset: Offset,
        animationSpec: AnimationSpec<Float> = spring(),
    ): Offset = coroutineScope {
        val xOffset = async {
            horizontalScrollState.animateScrollBy(offset.x, animationSpec)
        }
        val yOffset = async {
            verticalScrollState.animateScrollBy(offset.y, animationSpec)
        }
        Offset(xOffset.await(), yOffset.await())
    }

    /**
     * Scroll to the given position in pixels with animation.
     *
     * @param x the horizontal position to scroll to
     * @param y the vertical position to scroll to
     * @param animationSpec [AnimationSpec] to be used for this scrolling
     */
    suspend fun animateScrollTo(
        x: Int,
        y: Int,
        animationSpec: AnimationSpec<Float> = spring(),
    ) = coroutineScope {
        val xOffset = async {
            horizontalScrollState.animateScrollTo(x, animationSpec)
        }
        val yOffset = async {
            verticalScrollState.animateScrollTo(y, animationSpec)
        }
        awaitAll(xOffset, yOffset)
    }
}

/**
 * Create and [remember] [FreeScrollState] that is used to control and observe scrolling
 *
 * @param initialX initial horizontal scroller position
 * @param initialY initial vertical scroller position
 */
@Composable
fun rememberFreeScrollState(initialX: Int = 0, initialY: Int = 0): FreeScrollState {
    val horizontalScrollState = rememberScrollState(initialX)
    val verticalScrollState = rememberScrollState(initialY)
    return FreeScrollState(
        horizontalScrollState = horizontalScrollState,
        verticalScrollState = verticalScrollState,
    )
}
