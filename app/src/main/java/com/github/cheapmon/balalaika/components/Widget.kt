package com.github.cheapmon.balalaika.components

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.PresentToAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.theme.*
import com.github.cheapmon.balalaika.util.DarkThemeProvider
import com.github.cheapmon.balalaika.util.ResourceUtil
import com.github.cheapmon.balalaika.util.exhaustive
import com.github.cheapmon.balalaika.util.fullEntry

typealias PropertyAction<T> = (DataCategory, T, String) -> Unit

fun <T : Property> emptyPropertyAction(): PropertyAction<T> = { _, _, _ -> }

@Composable
fun WidgetFor(
    category: DataCategory,
    properties: List<Property>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onEvent: PropertyAction<Property> = emptyPropertyAction()
) {
    when (properties.firstOrNull()) {
        is Property.Audio -> {
            AudioWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Audio>(),
                enabled = enabled,
                modifier = modifier,
                transformText = transformText,
                onAudio = onEvent
            )
        }
        is Property.Example -> {
            ExampleWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Example>(),
                modifier = modifier,
                transformText = transformText
            )
        }
        is Property.Morphology -> {
            MorphologyWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Morphology>(),
                enabled = enabled,
                modifier = modifier,
                transformText = transformText,
                onMorphology = onEvent
            )
        }
        is Property.Plain -> {
            PlainWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Plain>(),
                enabled = enabled,
                transformText = transformText,
                onPlain = onEvent
            )
        }
        is Property.Reference -> {
            ReferenceWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Reference>(),
                enabled = enabled,
                modifier = modifier,
                transformText = transformText,
                onReference = onEvent
            )
        }
        is Property.Simple -> {
            SimpleWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Simple>(),
                enabled = enabled,
                modifier = modifier,
                transformText = transformText,
                onSimple = onEvent
            )
        }
        is Property.Url -> {
            UrlWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Url>(),
                enabled = enabled,
                modifier = modifier,
                transformText = transformText,
                onUrl = onEvent
            )
        }
        is Property.Wordnet -> {
            WordnetWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Wordnet>(),
                enabled = enabled,
                modifier = modifier,
                transformText = transformText,
                onWordnet = onEvent
            )
        }
        null -> {
        }
    }.exhaustive
}

@OptIn(ExperimentalLayout::class)
@Composable
private fun <T : Property> DefaultWidget(
    category: DataCategory,
    properties: List<T>,
    modifier: Modifier = Modifier,
    item: @Composable (T) -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.padding(vertical = itemPadding / 2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            val id = ResourceUtil.drawable(ContextAmbient.current, category.iconName)

            Icon(asset = vectorResource(id = id))
            Text(text = category.name, style = MaterialTheme.typography.body1)
        }
        Box(modifier = Modifier.preferredWidthIn(max = itemMaxWidth)) {
            FlowRow(mainAxisAlignment = FlowMainAxisAlignment.End) {
                properties.forEach { property ->
                    item(property)
                }
            }
        }
    }
}

@Composable
private fun <T : Property> ActionItem(
    category: DataCategory,
    property: T,
    text: String,
    icon: VectorAsset? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onAction: PropertyAction<T> = emptyPropertyAction()
) {
    Row(
        modifier = modifier
            .clip(simpleShape)
            .clickable(onClick = { onAction(category, property, text) }, enabled = enabled)
            .padding(itemPadding / 2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        Text(text = transformText(text), style = MaterialTheme.typography.body2)
        if (icon != null) Icon(asset = icon)
    }
}

@Composable
private fun AudioWidget(
    category: DataCategory,
    properties: List<Property.Audio>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onAudio: PropertyAction<Property.Audio> = emptyPropertyAction()
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(
            category = category,
            property = property,
            text = property.name,
            icon = Icons.Default.PlayCircleOutline,
            enabled = enabled,
            transformText = transformText,
            onAction = onAudio
        )
    }
}

@Composable
private fun ExampleWidget(
    category: DataCategory,
    properties: List<Property.Example>,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) }
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.padding(vertical = itemPadding / 2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            val id = ResourceUtil.drawable(ContextAmbient.current, category.iconName)

            Icon(asset = vectorResource(id = id))
            Text(text = category.name, style = MaterialTheme.typography.body1)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            horizontalAlignment = Alignment.End
        ) {
            properties.forEach { property ->
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = transformText(property.name),
                        style = MaterialTheme.typography.caption,
                        color = SubtitleColor()
                    )
                    Text(
                        text = transformText(property.content),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}

@Composable
private fun MorphologyWidget(
    category: DataCategory,
    properties: List<Property.Morphology>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onMorphology: PropertyAction<Property.Morphology> = emptyPropertyAction()
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        property.parts.forEach { part ->
            ActionItem(
                category = category,
                property = property,
                text = part,
                enabled = enabled,
                transformText = transformText,
                onAction = onMorphology
            )
        }
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
private fun PlainWidget(
    category: DataCategory,
    properties: List<Property.Plain>,
    enabled: Boolean = true,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onPlain: PropertyAction<Property.Plain> = emptyPropertyAction()
) {
    FlowRow(mainAxisSpacing = itemSpacing) {
        properties.forEach { property ->
            ActionItem(
                category = category,
                property = property,
                text = property.value,
                enabled = enabled,
                transformText = transformText,
                onAction = onPlain
            )
        }
    }
}

@Composable
private fun ReferenceWidget(
    category: DataCategory,
    properties: List<Property.Reference>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onReference: PropertyAction<Property.Reference> = emptyPropertyAction()
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(
            category = category,
            property = property,
            text = property.entry.representation,
            icon = Icons.Default.NorthEast,
            enabled = enabled,
            transformText = transformText,
            onAction = onReference
        )
    }
}

@Composable
private fun SimpleWidget(
    category: DataCategory,
    properties: List<Property.Simple>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onSimple: PropertyAction<Property.Simple> = emptyPropertyAction()
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(
            category = category,
            property = property,
            text = property.value,
            enabled = enabled,
            transformText = transformText,
            onAction = onSimple
        )
    }
}

@Composable
private fun UrlWidget(
    category: DataCategory,
    properties: List<Property.Url>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onUrl: PropertyAction<Property.Url> = emptyPropertyAction()
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(
            category = category,
            property = property,
            text = property.name,
            icon = Icons.Default.Link,
            enabled = enabled,
            transformText = transformText,
            onAction = onUrl
        )
    }
}

@Composable
private fun WordnetWidget(
    category: DataCategory,
    properties: List<Property.Wordnet>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onWordnet: PropertyAction<Property.Wordnet> = emptyPropertyAction()
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(
            category = category,
            property = property,
            text = property.name,
            icon = Icons.Default.PresentToAll,
            enabled = enabled,
            transformText = transformText,
            onAction = onWordnet
        )
    }
}

@Preview
@Composable
private fun WidgetPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Card(modifier = Modifier.padding(vertical = itemSpacing)) {
            Column(modifier = Modifier.padding(itemPadding)) {
                fullEntry.properties.forEach { (category, list) ->
                    WidgetFor(category = category, properties = list)
                }
            }
        }
    }
}
