# Compose Free Scroll

Jetpack Compose library for free scroll (diagonal scroll) modifier

[![](https://jitpack.io/v/chihsuanwu/compose-free-scroll.svg)](https://jitpack.io/#chihsuanwu/compose-free-scroll)

This library provides a freeScroll modifier that allows scrolling in any direction, as opposed to the official `horizontalScroll` and `verticalScroll` modifiers that only allow scrolling in one direction at a time. With the `freeScroll` modifier, you can scroll in any direction simultaneously.

To use the freeScroll modifier, add the following dependencies to your project:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.chihsuanwu:compose-free-scroll:0.1.0'
}
```

Then, in your composable function, include the following code:

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

You can also enable or disable the scrolling by passing in a enabled parameter to the freeScroll modifier.

# Limitations

This library is still in its early stage and many features are not yet implemented.
Such as **reverse scrolling direction**, **bounce effect** and **fling behavior modification**.

Any contributions are welcome!
