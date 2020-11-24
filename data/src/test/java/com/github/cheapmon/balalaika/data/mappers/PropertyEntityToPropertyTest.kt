package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class PropertyEntityToPropertyTest {
    private val dictionaryEntryDao = mock(DictionaryEntryDao::class.java)
    private val toDictionaryEntry = mock(DictionaryEntryEntityToDictionaryEntryMapper::class.java)

    private val entry = DictionaryEntry(
        id = "lex_a",
        representation = "Lexeme A",
        base = null,
        properties = TreeMap(),
        bookmark = null
    )

    private val makeTriple = { value: String, widgetType: WidgetType, prop: Property ->
        Triple(
            PropertyEntity(1, "cat_a", "dic_a", "lex_a", value),
            CategoryEntity("cat_a", "dic_a", "", widgetType, "", 0, false, false),
            prop
        )
    }
    private val values: List<Triple<PropertyEntity, CategoryEntity, Property>> = listOf(
        makeTriple("name;;;value", WidgetType.AUDIO, Property.Audio("name", "value")),
        makeTriple("name;;;value", WidgetType.EXAMPLE, Property.Example("name", "value")),
        makeTriple("value", WidgetType.KEY_VALUE, Property.Simple("value")),
        makeTriple("a|b|c", WidgetType.MORPHOLOGY, Property.Morphology(listOf("a", "b", "c"))),
        makeTriple("value", WidgetType.PLAIN, Property.Plain("value")),
        makeTriple("name;;;lex_a", WidgetType.REFERENCE, Property.Reference(entry)),
        makeTriple("name;;;value", WidgetType.URL, Property.Url("name", "value")),
        makeTriple("name;;;value", WidgetType.WORDNET, Property.Wordnet("name", "value"))
    )

    private val mapper = PropertyEntityToProperty(dictionaryEntryDao)

    @Test
    fun mapsToProperty() = runBlockingTest {
        val entity = DictionaryEntryEntity(
            lexeme = LexemeEntity(
                id = "lex_a",
                dictionaryId = "dic_a",
                form = "Lexeme A",
                baseId = null
            ),
            base = null,
            properties = emptyList(),
            bookmark = null
        )
        val view = DictionaryViewWithCategories(
            dictionaryView = DictionaryViewEntity(
                id = "view_a",
                dictionaryId = "dic_a",
                name = "View A"
            ),
            categories = emptyList()
        )

        `when`(dictionaryEntryDao.findEntryById("dic_a", "lex_a"))
            .thenReturn(entity)
        `when`(toDictionaryEntry(entity, view))
            .thenReturn(entry)

        for ((prop, category, expected) in values) {
            val result = mapper(prop, category, view, toDictionaryEntry)
            Assert.assertEquals(expected, result)
        }
    }
}
