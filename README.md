# Compose Free Scroll

Jetpack Compose library for free scroll (diagonal scroll) modifier

[![](https://jitpack.io/v/chihsuanwu/compose-free-scroll.svg)](https://jitpack.io/#chihsuanwu/compose-free-scroll)


With the official `horizontalScroll` and `verticalScroll` modifier, we can only scroll in one direction. This library provides a `freeScroll` modifier that allows us to scroll in any direction.

A simple example:

```kotlin
@Composable
fun FreeScrollExample() {
    val freeScrollState = rememberFreeScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .freeScroll(state = freeScrollState)
    ) {
        // Content ...
    }
}
```

# Installation

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.chihsuanwu:compose-free-scroll:0.0.4'
}
```

# Usage

```
Modifier
    .freeScroll(
        state: FreeScrollState,
        enabled: Boolean = true
    )
```


# Limitations

This library is still in its early stage. Many features are not implemented yet. 

Roadmap:

- [x] Basic free scroll
- [ ] Expose scroll state
- [ ] Enable/disable scroll
- [ ] Fling behavior
- [ ] Reverse scroll direction
- [ ] Bounce effect
- [ ] Scroll to offset programmatically
