package com.github.cheapmon.balalaika.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.material.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.theme.*
import com.github.cheapmon.balalaika.util.DarkThemeProvider

enum class CollapsibleCardState {
    COLLAPSED, EXPANDED
}

private fun CollapsibleCardState.flip(): CollapsibleCardState =
    if (this == CollapsibleCardState.COLLAPSED) {
        CollapsibleCardState.EXPANDED
    } else {
        CollapsibleCardState.COLLAPSED
    }

@Composable
fun CollapsibleCard(
    id: Any? = null,
    initialState: CollapsibleCardState = CollapsibleCardState.COLLAPSED,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    actions: @Composable () -> Unit = {},
    body: @Composable () -> Unit = {}
) {
    var state by remember(id) { mutableStateOf(initialState) }

    val rotationX = FloatPropKey(label = "rotationX")
    val transitionState = createTransition(state = state, rotationX = rotationX)

    Card(modifier = Modifier.padding(vertical = itemSpacing).fillMaxWidth().then(modifier)) {
        Column(modifier = Modifier.padding(itemPadding).animateContentSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.preferredWidthIn(max = headerMaxWidth)) {
                    header()
                }
                Row {
                    actions()
                    IconButton(onClick = { state = state.flip() }) {
                        Icon(
                            asset = Icons.Default.KeyboardArrowUp,
                            modifier = Modifier.drawLayer(rotationX = transitionState[rotationX]),
                            tint = MaterialColors.onSurfaceLight
                        )
                    }
                }
            }
            if (state == CollapsibleCardState.EXPANDED) {
                Spacer(modifier = Modifier.preferredHeight(headerGap))
                body()
            }
        }
    }
}

@Composable
private fun createTransition(
    state: CollapsibleCardState,
    rotationX: FloatPropKey
): TransitionState {
    val transitionDefinition = transitionDefinition<CollapsibleCardState> {
        state(CollapsibleCardState.COLLAPSED) {
            this[rotationX] = 0f
        }

        state(CollapsibleCardState.EXPANDED) {
            this[rotationX] = 180f
        }

        transition {
            rotationX using tween(durationMillis = 200, easing = FastOutSlowInEasing)
        }
    }

    return transition(
        definition = transitionDefinition,
        initState = state,
        toState = state.flip()
    )
}

@Preview
@Composable
private fun CollapsibleCardPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        CollapsibleCard(
            initialState = CollapsibleCardState.EXPANDED,
            header = {
                Column {
                    Text(text = "This is a headline", style = MaterialTypography.h6)
                    Text(text = "This is a subtitle", style = MaterialTypography.subtitle1)
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(asset = Icons.Default.Book)
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.impsum),
                style = MaterialTypography.body2,
                textAlign = TextAlign.Justify
            )
        }
    }
}
