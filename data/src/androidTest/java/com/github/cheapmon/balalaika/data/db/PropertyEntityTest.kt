package com.github.cheapmon.balalaika.data.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyWithCategory
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewToCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class PropertyEntityTest {
    private lateinit var db: AppDatabase

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

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

    private val lexemeA = LexemeEntity(
        id = "lex_a",
        dictionaryId = dictionaryA.id,
        form = "Lexeme A",
        baseId = null
    )

    private val propertyA = PropertyEntity(
        id = 1,
        categoryId = categoryA.id,
        dictionaryId = dictionaryA.id,
        lexemeId = lexemeA.id,
        value = "Property A"
    )

    private val propertyB = PropertyEntity(
        id = 2,
        categoryId = categoryB.id,
        dictionaryId = dictionaryA.id,
        lexemeId = lexemeA.id,
        value = "Property B"
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
    fun createDb(): Unit = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.dictionaries().insertAll(listOf(dictionaryA))
        db.categories().insertAll(listOf(categoryA, categoryB))
        db.lexemes().insertAll(listOf(lexemeA))
        db.properties().insertAll(listOf(propertyA, propertyB))
        db.dictionaryViews().insertViews(listOf(viewAll, viewB))
        db.dictionaryViews().insertRelation(listOf(relationA, relationB, relationC))
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getProperties(): Unit = runBlockingTest {
        val aWithCategory = PropertyWithCategory(
            property = propertyA,
            category = categoryA
        )
        val bWithCategory = PropertyWithCategory(
            property = propertyB,
            category = categoryB
        )

        Assert.assertEquals(
            listOf(aWithCategory, bWithCategory),
            db.properties().getProperties(lexemeA.id, viewAll.id)
        )
        Assert.assertEquals(
            listOf(bWithCategory),
            db.properties().getProperties(lexemeA.id, viewB.id)
        )
    }

    @Test
    fun insertProperty(): Unit = runBlockingTest {
        val propertyC = PropertyEntity(
            id = 3,
            categoryId = categoryA.id,
            dictionaryId = dictionaryA.id,
            lexemeId = lexemeA.id,
            value = "Property C"
        )
        db.properties().insertAll(listOf(propertyC))

        val aWithCategory = PropertyWithCategory(
            property = propertyA,
            category = categoryA
        )
        val bWithCategory = PropertyWithCategory(
            property = propertyB,
            category = categoryB
        )
        val cWithCategory = PropertyWithCategory(
            property = propertyC,
            category = categoryA
        )
        Assert.assertEquals(
            listOf(aWithCategory, bWithCategory, cWithCategory),
            db.properties().getProperties(lexemeA.id, viewAll.id)
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnId(): Unit = runBlockingTest {
        val faultyProperty = PropertyEntity(
            id = 1,
            categoryId = categoryA.id,
            dictionaryId = dictionaryA.id,
            lexemeId = lexemeA.id,
            value = "Property"
        )
        db.properties().insertAll(listOf(faultyProperty))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnCategoryId(): Unit = runBlockingTest {
        val faultyProperty = PropertyEntity(
            id = 3,
            categoryId = "garbage",
            dictionaryId = dictionaryA.id,
            lexemeId = lexemeA.id,
            value = "Property"
        )
        db.properties().insertAll(listOf(faultyProperty))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnDictionaryId(): Unit = runBlockingTest {
        val faultyProperty = PropertyEntity(
            id = 3,
            categoryId = categoryA.id,
            dictionaryId = "garbage",
            lexemeId = lexemeA.id,
            value = "Property"
        )
        db.properties().insertAll(listOf(faultyProperty))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnLexemeId(): Unit = runBlockingTest {
        val faultyProperty = PropertyEntity(
            id = 3,
            categoryId = categoryA.id,
            dictionaryId = dictionaryA.id,
            lexemeId = "garbage",
            value = "Property"
        )
        db.properties().insertAll(listOf(faultyProperty))
    }

    @Test
    fun removeProperties(): Unit = runBlockingTest {
        db.properties().removeInDictionary(dictionaryA.id)
        Assert.assertEquals(
            emptyList<PropertyWithCategory>(),
            db.properties().getProperties(lexemeA.id, viewAll.id)
        )
    }
}
