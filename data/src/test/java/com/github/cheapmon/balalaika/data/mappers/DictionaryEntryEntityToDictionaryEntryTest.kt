package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyWithCategory
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.model.Bookmark
import com.github.cheapmon.balalaika.model.DataCategory
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
internal class DictionaryEntryEntityToDictionaryEntryTest {
    private val dictionaryA = DictionaryEntity(
        id = "dic_a",
        version = 0,
        name = "Dictionary A",
        summary = "",
        authors = "",
        additionalInfo = ""
    )

    private val categoryA = CategoryEntity(
        id = "cat_a",
        dictionaryId = dictionaryA.id,
        name = "Category A",
        widget = WidgetType.PLAIN,
        iconName = "ic_circle",
        sequence = 0,
        hidden = false,
        sortable = false
    )

    private val categoryB = CategoryEntity(
        id = "cat_b",
        dictionaryId = dictionaryA.id,
        name = "Category B",
        widget = WidgetType.PLAIN,
        iconName = "ic_circle",
        sequence = 0,
        hidden = false,
        sortable = false
    )

    private val base = LexemeEntity(
        id = "base",
        dictionaryId = dictionaryA.id,
        form = "Base",
        baseId = null
    )

    private val lexemeA = LexemeEntity(
        id = "lex_a",
        dictionaryId = dictionaryA.id,
        form = "Lexeme A",
        baseId = base.id
    )

    private val propertyA = PropertyWithCategory(
        property = PropertyEntity(
            id = 1,
            categoryId = categoryA.id,
            dictionaryId = dictionaryA.id,
            lexemeId = lexemeA.id,
            value = "Property A"
        ),
        category = categoryA
    )

    private val propertyB = PropertyWithCategory(
        property = PropertyEntity(
            id = 2,
            categoryId = categoryB.id,
            dictionaryId = dictionaryA.id,
            lexemeId = lexemeA.id,
            value = "Property B"
        ),
        category = categoryB
    )

    private val dictionaryEntryA = DictionaryEntryEntity(
        lexeme = lexemeA,
        base = base,
        properties = listOf(propertyA, propertyB),
        bookmark = BookmarkEntity(1, dictionaryA.id, lexemeA.id)
    )

    private val viewAll = DictionaryViewWithCategories(
        dictionaryView = DictionaryViewEntity(
            id = "view_a",
            dictionaryId = dictionaryA.id,
            name = "View A"
        ),
        categories = listOf(categoryA, categoryB)
    )

    private val viewB = DictionaryViewWithCategories(
        dictionaryView = DictionaryViewEntity(
            id = "view_b",
            dictionaryId = dictionaryA.id,
            name = "View B"
        ),
        categories = listOf(categoryB)
    )

    private val dao = mock(DictionaryEntryDao::class.java)

    private val mapper = DictionaryEntryEntityToDictionaryEntry(
        dictionaryEntryDao = dao,
        toDataCategory = CategoryEntityToDataCategory(),
        toProperty = PropertyEntityToProperty(dao)
    )

    @Test
    fun mapsToDictionaryEntry() = runBlockingTest {
        `when`(dao.findEntryById("dic_a", "base")).thenReturn(
            DictionaryEntryEntity(
                lexeme = base,
                base = null,
                properties = listOf(),
                bookmark = null
            )
        )
        val result = mapper(dictionaryEntryA, viewAll)
        val expected = DictionaryEntry(
            id = "lex_a",
            representation = "Lexeme A",
            base = DictionaryEntry(
                id = "base",
                representation = "Base",
                base = null,
                properties = sortedProperties(),
                bookmark = null
            ),
            properties = sortedProperties(
                DataCategory("cat_a", "Category A", "ic_circle", 0) to
                        listOf(Property.Plain("Property A")),
                DataCategory("cat_b", "Category B", "ic_circle", 0) to
                        listOf(Property.Plain("Property B"))
            ),
            bookmark = Bookmark()
        )
        Assert.assertEquals(expected, result)
        val resultingProperties = mapper(dictionaryEntryA, viewB).properties
        val expectedProperties = sortedProperties(
            DataCategory("cat_b", "Category B", "ic_circle", 0) to
                    listOf(Property.Plain("Property B"))
        )
        Assert.assertEquals(expectedProperties, resultingProperties)
    }

    private fun sortedProperties(
        vararg pairs: Pair<DataCategory, List<Property>>
    ): SortedMap<DataCategory, List<Property>> {
        return mapOf(*pairs).toSortedMap { o1, o2 -> o1.sequence.compareTo(o2.sequence) }
    }
}
