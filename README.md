# Compose Free Scroll

Jetpack Compose library for free scroll (diagonal scroll) modifier

[![](https://jitpack.io/v/chihsuanwu/compose-free-scroll.svg)](https://jitpack.io/#chihsuanwu/compose-free-scroll)

This library provides a `freeScroll` modifier that allows scrolling in any direction,
as opposed to the official `horizontalScroll` and `verticalScroll` modifiers that
only allow scrolling in one direction at a time. With the `freeScroll` modifier,
you can scroll in any direction simultaneously.

[demo](https://user-images.githubusercontent.com/22000682/214777145-a7b2cbdd-c780-47a2-bb5d-c46e2e02b93f.mp4)

# Installation

In your project's root build.gradle file, add the following:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

In your app's build.gradle file, add the following:

```groovy
dependencies {
    implementation 'com.github.chihsuanwu:compose-free-scroll:0.2.1'
}
```

# Usage

To use the freeScroll modifier, simply add it to the modifier chain of a composable that you want to be scrollable.

```kotlin
val freeScrollState = rememberFreeScrollState()
Column(
    modifier = Modifier
        .fillMaxSize()
        .freeScroll(state = freeScrollState)
) {
    // Content ...
}
```

Note that this modifier uses `pointerInput` as the underlying implementation, so some
pointer events will be consumed.

If you want to use `TransformGestures` simultaneously, you can use the `freeScrollWithTransformGesture` modifier.

```kotlin
val freeScrollState = rememberFreeScrollState()
Column(
    modifier = Modifier
        .fillMaxSize()
        .freeScrollWithTransformGesture(
            state = freeScrollState,
            onGesture = { centroid: Offset,
                          pan: Offset,
                          zoom: Float,
                          rotation: Float ->
                // Transform gestures ...
            }
        )
) {
    // Content ...
}
```

# Limitations

Currently, this library still lacks a bounce effect feature. This is limited by the
current implementation.

Any contributions are highly appreciated!
