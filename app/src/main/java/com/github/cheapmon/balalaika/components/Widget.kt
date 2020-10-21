package com.github.cheapmon.balalaika.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
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
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.theme.*
import com.github.cheapmon.balalaika.util.ResourceUtil
import com.github.cheapmon.balalaika.util.exhaustive
import com.github.cheapmon.balalaika.util.fullEntry

@Composable
fun WidgetFor(
    category: DataCategory,
    properties: List<Property>,
    modifier: Modifier = Modifier,
    onEvent: (Property) -> Unit = {}
) {
    when (properties.firstOrNull()) {
        is Property.Audio -> {
            AudioWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Audio>(),
                modifier = modifier,
                onAudio = onEvent
            )
        }
        is Property.Example -> {
            ExampleWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Example>(),
                modifier = modifier
            )
        }
        is Property.Morphology -> {
            MorphologyWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Morphology>(),
                modifier = modifier,
                onMorphology = onEvent
            )
        }
        is Property.Plain -> {
            PlainWidget(
                properties = properties.filterIsInstance<Property.Plain>(),
                onPlain = onEvent
            )
        }
        is Property.Reference -> {
            ReferenceWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Reference>(),
                modifier = modifier,
                onReference = onEvent
            )
        }
        is Property.Simple -> {
            SimpleWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Simple>(),
                modifier = modifier,
                onSimple = onEvent
            )
        }
        is Property.Url -> {
            UrlWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Url>(),
                modifier = modifier,
                onUrl = onEvent
            )
        }
        is Property.Wordnet -> {
            WordnetWidget(
                category = category,
                properties = properties.filterIsInstance<Property.Wordnet>(),
                modifier = modifier,
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
    property: T,
    text: String,
    icon: VectorAsset? = null,
    modifier: Modifier = Modifier,
    onAction: (T) -> Unit = {}
) {
    Row(
        modifier = modifier
            .clip(simpleShape)
            .clickable(onClick = { onAction(property) })
            .padding(itemPadding / 2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        Text(text = text, style = MaterialTheme.typography.body2)
        if (icon != null) Icon(asset = icon)
    }
}


@Composable
private fun AudioWidget(
    category: DataCategory,
    properties: List<Property.Audio>,
    modifier: Modifier = Modifier,
    onAudio: (Property.Audio) -> Unit = {}
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(
            property = property,
            text = property.name,
            icon = Icons.Default.PlayCircleOutline,
            onAction = onAudio
        )
    }
}

@Composable
private fun ExampleWidget(
    category: DataCategory,
    properties: List<Property.Example>,
    modifier: Modifier = Modifier
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
                        text = property.name,
                        style = MaterialTheme.typography.caption,
                        color = SubtitleColor()
                    )
                    Text(text = property.content, style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}

@Composable
private fun MorphologyWidget(
    category: DataCategory,
    properties: List<Property.Morphology>,
    modifier: Modifier = Modifier,
    onMorphology: (Property.Morphology) -> Unit = {}
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        property.parts.forEach { part ->
            ActionItem(property = property, text = part, onAction = onMorphology)
        }
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
private fun PlainWidget(
    properties: List<Property.Plain>,
    onPlain: (Property.Plain) -> Unit = {}
) {
    FlowRow(mainAxisSpacing = itemSpacing) {
        properties.forEach { property ->
            ActionItem(property = property, text = property.value, onAction = onPlain)
        }
    }
}

@Composable
private fun ReferenceWidget(
    category: DataCategory,
    properties: List<Property.Reference>,
    modifier: Modifier = Modifier,
    onReference: (Property.Reference) -> Unit = {}
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(
            property = property,
            text = property.entry.representation,
            icon = Icons.Default.NorthEast,
            onAction = onReference
        )
    }
}

@Composable
private fun SimpleWidget(
    category: DataCategory,
    properties: List<Property.Simple>,
    modifier: Modifier = Modifier,
    onSimple: (Property.Simple) -> Unit = {}
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(property = property, text = property.value, onAction = onSimple)
    }
}

@Composable
private fun UrlWidget(
    category: DataCategory,
    properties: List<Property.Url>,
    modifier: Modifier = Modifier,
    onUrl: (Property.Url) -> Unit = {}
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(
            property = property,
            text = property.name,
            icon = Icons.Default.Link,
            onAction = onUrl
        )
    }
}

@Composable
private fun WordnetWidget(
    category: DataCategory,
    properties: List<Property.Wordnet>,
    modifier: Modifier = Modifier,
    onWordnet: (Property.Wordnet) -> Unit = {}
) {
    DefaultWidget(category = category, properties = properties, modifier = modifier) { property ->
        ActionItem(
            property = property,
            text = property.name,
            icon = Icons.Default.PresentToAll,
            onAction = onWordnet
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WidgetPreview() {
    BalalaikaTheme {
        Card(modifier = Modifier.padding(vertical = itemSpacing)) {
            Column(modifier = Modifier.padding(itemPadding)) {
                fullEntry.properties.forEach { (category, list) ->
                    WidgetFor(category = category, properties = list)
                }
            }
        }
    }
}