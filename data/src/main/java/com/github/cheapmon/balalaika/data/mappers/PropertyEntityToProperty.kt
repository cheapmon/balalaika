package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.model.Property
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PropertyEntityToProperty @Inject constructor(
    private val dictionaryEntryDao: DictionaryEntryDao
) {
    suspend operator fun invoke(
        property: PropertyEntity,
        category: CategoryEntity,
        view: DictionaryViewWithCategories,
        toDictionaryEntry: DictionaryEntryEntityToDictionaryEntryMapper
    ): Property? {
        return when (category.widget) {
            WidgetType.AUDIO -> {
                extractNameAndValue(property.value)?.let { (name, value) ->
                    Property.Audio(name, value)
                }
            }
            WidgetType.EXAMPLE -> {
                extractNameAndValue(property.value)?.let { (name, value) ->
                    Property.Example(name, value)
                }
            }
            WidgetType.KEY_VALUE -> Property.Simple(property.value)
            WidgetType.MORPHOLOGY -> Property.Morphology(property.value.split("|"))
            WidgetType.PLAIN -> Property.Plain(property.value)
            WidgetType.REFERENCE -> extractNameAndValue(property.value)?.let { (_, value) ->
                dictionaryEntryDao.findEntryById(property.dictionaryId, value)?.let { entity ->
                    Property.Reference(toDictionaryEntry(entity, view))
                }
            }
            WidgetType.URL -> extractNameAndValue(property.value)?.let { (name, value) ->
                Property.Url(name, value)
            }
            WidgetType.WORDNET -> extractNameAndValue(property.value)?.let { (name, value) ->
                Property.Wordnet(name, value)
            }
        }
    }

    private fun extractNameAndValue(input: String): Pair<String, String>? {
        val parts = input.split(";;;")
        val name = parts.getOrNull(0)
        val value = parts.getOrNull(1)
        return if (name != null && value != null) {
            Pair(name, value)
        } else {
            null
        }
    }
}
