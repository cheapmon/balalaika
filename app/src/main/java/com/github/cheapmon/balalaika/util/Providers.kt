package com.github.cheapmon.balalaika.util

import androidx.ui.tooling.preview.PreviewParameterProvider

class DarkThemeProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(false, true)
}

class PreviewParameterProviderCombiner<A, B, C>(
    first: PreviewParameterProvider<A>,
    second: PreviewParameterProvider<B>,
    block: (A, B) -> C
) : PreviewParameterProvider<C> {
    override val values: Sequence<C> = first.values.flatMap { a ->
        second.values.map { b -> block(a, b) }
    }
}
