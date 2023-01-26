# Compose Free Scroll

Jetpack Compose library for free scroll (diagonal scroll) modifier

[![](https://jitpack.io/v/chihsuanwu/compose-free-scroll.svg)](https://jitpack.io/#chihsuanwu/compose-free-scroll)

This library provides a `freeScroll` modifier that allows scrolling in any direction, as opposed to the official `horizontalScroll` and `verticalScroll` modifiers that only allow scrolling in one direction at a time. With the `freeScroll` modifier, you can scroll in any direction simultaneously.

[demo](https://user-images.githubusercontent.com/22000682/214753295-274ddb01-ebe3-4db6-8143-68211160b264.webm)


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
    implementation 'com.github.chihsuanwu:compose-free-scroll:0.1.2'
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

This library is still in its early stage and many features are not yet implemented.
Such as **reverse scrolling direction**, **bounce effect** and **fling behavior modification**.

Any contributions are highly appreciated!
