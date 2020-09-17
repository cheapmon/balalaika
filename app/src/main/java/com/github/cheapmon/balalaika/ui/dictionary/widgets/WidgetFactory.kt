package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.ViewGroup
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Property

class WidgetFactory(
    private val parent: ViewGroup,
    private val hasActions: Boolean = false,
    private val menuListener: WidgetMenuListener = emptyMenuListener,
    private val audioActionListener: WidgetActionListener<Property.Audio> = emptyActionListener(),
    private val referenceActionListener: WidgetActionListener<Property.Reference> = emptyActionListener(),
    private val urlActionListener: WidgetActionListener<Property.Url> = emptyActionListener(),
    private val wordnetActionListener: WidgetActionListener<Property.Wordnet> = emptyActionListener()
) {
    /**
     * _Note_: This relies for all properties in the list to have the same type, which _should_
     * be true but can't be enforced at the moment. There might be a better way to implement
     * this behaviour.
     */
    fun get(
        category: DataCategory,
        properties: List<Property>
    ): Widget<*> = when (properties.first()) {
        is Property.Audio -> AudioWidget(
            parent,
            category,
            properties.filterIsInstance<Property.Audio>(),
            hasActions,
            menuListener,
            audioActionListener
        )
        is Property.Example -> ExampleWidget(
            parent,
            category,
            properties.filterIsInstance<Property.Example>(),
            menuListener
        )
        is Property.Morphology -> MorphologyWidget(
            parent,
            category,
            properties.filterIsInstance<Property.Morphology>(),
            menuListener
        )
        is Property.Plain -> PlainWidget(
            parent,
            category,
            properties.filterIsInstance<Property.Plain>(),
            menuListener
        )
        is Property.Reference -> ReferenceWidget(
            parent,
            category,
            properties.filterIsInstance<Property.Reference>(),
            hasActions,
            menuListener,
            referenceActionListener
        )
        is Property.Simple -> SimpleWidget(
            parent,
            category,
            properties.filterIsInstance<Property.Simple>(),
            menuListener
        )
        is Property.Url -> UrlWidget(
            parent,
            category,
            properties.filterIsInstance<Property.Url>(),
            hasActions,
            menuListener,
            urlActionListener
        )
        is Property.Wordnet -> WordnetWidget(
            parent,
            category,
            properties.filterIsInstance<Property.Wordnet>(),
            hasActions,
            menuListener,
            wordnetActionListener
        )
    }

    private companion object {
        val emptyMenuListener: WidgetMenuListener = object : WidgetMenuListener {
            override fun onClickMenuItem(item: String, category: DataCategory) = Unit
        }

        fun <T : Property> emptyActionListener(): WidgetActionListener<T> =
            object : WidgetActionListener<T> {
                override fun onAction(property: T) = Unit
            }
    }
}
