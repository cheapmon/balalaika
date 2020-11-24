package com.github.cheapmon.balalaika.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyWithCategory
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewToCategory
import com.github.cheapmon.balalaika.data.util.TestCoroutineDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class DictionaryEntryEntityTest {
    private lateinit var db: AppDatabase

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = TestCoroutineDispatcherRule()

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
        sequence = 1,
        hidden = false,
        sortable = false
    )

    private val lexemeA = LexemeEntity(
        id = "lex_a",
        dictionaryId = dictionaryA.id,
        form = "Lexeme BBB",
        baseId = null
    )

    private val lexemeB = LexemeEntity(
        id = "lex_b",
        dictionaryId = dictionaryA.id,
        form = "Lexeme AAA",
        baseId = lexemeA.id
    )

    private val bookmarkB = BookmarkEntity(
        id = 1,
        dictionaryId = dictionaryA.id,
        lexemeId = lexemeB.id
    )

    private val propertyA = PropertyEntity(
        id = 1,
        categoryId = categoryA.id,
        dictionaryId = dictionaryA.id,
        lexemeId = lexemeB.id,
        value = "Property BBB"
    )

    private val propertyB = PropertyEntity(
        id = 2,
        categoryId = categoryB.id,
        dictionaryId = dictionaryA.id,
        lexemeId = lexemeB.id,
        value = "Property B"
    )

    private val propertyC = PropertyEntity(
        id = 3,
        categoryId = categoryA.id,
        dictionaryId = dictionaryA.id,
        lexemeId = lexemeA.id,
        value = "Property AAA"
    )

    private val viewAll = DictionaryViewEntity(
        id = "view_a",
        dictionaryId = dictionaryA.id,
        name = "View A"
    )

    private val viewB = DictionaryViewEntity(
        id = "view_b",
        dictionaryId = dictionaryA.id,
        name = "View B"
    )

    private val relationA = DictionaryViewToCategory(
        id = viewAll.id,
        categoryId = categoryA.id,
        dictionaryId = dictionaryA.id
    )

    private val relationB = DictionaryViewToCategory(
        id = viewAll.id,
        categoryId = categoryB.id,
        dictionaryId = dictionaryA.id
    )

    private val relationC = DictionaryViewToCategory(
        id = viewB.id,
        categoryId = categoryB.id,
        dictionaryId = dictionaryA.id
    )

    @Before
    fun createDb(): Unit = coroutineRule.dispatcher.runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.dictionaries().insertAll(listOf(dictionaryA))
        db.categories().insertAll(listOf(categoryA, categoryB))
        db.lexemes().insertAll(listOf(lexemeA, lexemeB))
        db.bookmarks().insert(bookmarkB)
        db.properties().insertAll(listOf(propertyA, propertyB, propertyC))
        db.dictionaryViews().insertViews(listOf(viewAll, viewB))
        db.dictionaryViews().insertRelation(listOf(relationA, relationB, relationC))
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun findEntry(): Unit = coroutineRule.dispatcher.runBlockingTest {
        val propertyAWithCategory = PropertyWithCategory(
            property = propertyA,
            category = categoryA
        )
        val propertyBWithCategory = PropertyWithCategory(
            property = propertyB,
            category = categoryB
        )
        val entryB = DictionaryEntryEntity(
            lexeme = lexemeB,
            base = lexemeA,
            properties = listOf(propertyAWithCategory, propertyBWithCategory),
            bookmark = bookmarkB
        )
        Assert.assertEquals(
            entryB,
            db.dictionaryEntries().findEntryById(dictionaryA.id, lexemeB.id)
        )
        Assert.assertNull(db.dictionaryEntries().findEntryById(dictionaryA.id, "garbage"))
        Assert.assertNull(db.dictionaryEntries().findEntryById("garbage", lexemeB.id))
    }

    @Test
    fun getIds(): Unit = coroutineRule.dispatcher.runBlockingTest {
        Assert.assertEquals(
            listOf(lexemeB.id, lexemeA.id),
            db.dictionaryEntries().getLexemes(dictionaryA.id, viewAll.id)
        )
        Assert.assertEquals(
            listOf(lexemeA.id, lexemeB.id),
            db.dictionaryEntries().getLexemes(dictionaryA.id, viewAll.id, categoryA.id)
        )
    }

    @Test
    fun findEntries(): Unit = coroutineRule.dispatcher.runBlockingTest {
        val pagingSource = db.dictionaryEntries().findLexemes(dictionaryA.id, "Lexeme")
        val items = runBlocking { loadFromPagingSource(pagingSource, 2) }

        val propertyAWithCategory = PropertyWithCategory(
            property = propertyA,
            category = categoryA
        )
        val propertyBWithCategory = PropertyWithCategory(
            property = propertyB,
            category = categoryB
        )
        val propertyCWithCategory = PropertyWithCategory(
            property = propertyC,
            category = categoryA
        )
        val entryA = DictionaryEntryEntity(
            lexeme = lexemeA,
            base = null,
            properties = listOf(propertyCWithCategory),
            bookmark = null
        )
        val entryB = DictionaryEntryEntity(
            lexeme = lexemeB,
            base = lexemeA,
            properties = listOf(propertyAWithCategory, propertyBWithCategory),
            bookmark = bookmarkB
        )
        Assert.assertEquals(
            listOf(entryB, entryA),
            items
        )
    }

    @Test
    fun findEntriesWithRestriction(): Unit = coroutineRule.dispatcher.runBlockingTest {
        val pagingSource = db.dictionaryEntries().findLexemes(
            dictionaryId = dictionaryA.id,
            query = "Lexeme",
            categoryId = categoryA.id,
            restriction = "BBB"
        )
        val items = runBlocking { loadFromPagingSource(pagingSource, 2) }

        val propertyAWithCategory = PropertyWithCategory(
            property = propertyA,
            category = categoryA
        )
        val propertyBWithCategory = PropertyWithCategory(
            property = propertyB,
            category = categoryB
        )
        val entryB = DictionaryEntryEntity(
            lexeme = lexemeB,
            base = lexemeA,
            properties = listOf(propertyAWithCategory, propertyBWithCategory),
            bookmark = bookmarkB
        )
        Assert.assertEquals(
            listOf(entryB),
            items
        )
    }

    private suspend fun <K : Any, V : Any> loadFromPagingSource(
        source: PagingSource<K, V>,
        loadSize: Int
    ): List<V> {
        val params = PagingSource.LoadParams.Refresh<K>(null, loadSize, false)
        val loadResult = source.load(params)
        return if (loadResult is PagingSource.LoadResult.Page<K, V>) {
            loadResult.data
        } else {
            emptyList()
        }
    }
}
