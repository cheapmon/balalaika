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
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewToCategory
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class DictionaryViewEntityTest {
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

    private val viewAll = DictionaryViewEntity(
        id = "view_all",
        dictionaryId = dictionaryA.id,
        name = "View All"
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
        db.dictionaryViews().insertViews(listOf(viewAll, viewB))
        db.dictionaryViews().insertRelation(listOf(relationA, relationB, relationC))
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getViews(): Unit = runBlockingTest {
        val viewAllWithCategories = DictionaryViewWithCategories(
            dictionaryView = viewAll,
            categories = listOf(categoryA, categoryB)
        )
        val viewBWithCategories = DictionaryViewWithCategories(
            dictionaryView = viewB,
            categories = listOf(categoryB)
        )

        Assert.assertEquals(
            listOf(viewAllWithCategories, viewBWithCategories),
            db.dictionaryViews().getAll(dictionaryA.id).first()
        )
        Assert.assertEquals(
            emptyList<DictionaryViewWithCategories>(),
            db.dictionaryViews().getAll("garbage").first()
        )
    }

    @Test
    fun insertView(): Unit = runBlockingTest {
        val viewA = DictionaryViewEntity(
            id = "view_a",
            dictionaryId = dictionaryA.id,
            name = "View A"
        )
        val relation = DictionaryViewToCategory(
            id = viewA.id,
            categoryId = categoryA.id,
            dictionaryId = dictionaryA.id
        )

        val viewAllWithCategories = DictionaryViewWithCategories(
            dictionaryView = viewAll,
            categories = listOf(categoryA, categoryB)
        )
        val viewBWithCategories = DictionaryViewWithCategories(
            dictionaryView = viewB,
            categories = listOf(categoryB)
        )
        val viewAWithCategories = DictionaryViewWithCategories(
            dictionaryView = viewA,
            categories = listOf(categoryA)
        )
        db.dictionaryViews().insertViews(listOf(viewA))
        db.dictionaryViews().insertRelation(listOf(relation))
        Assert.assertEquals(
            listOf(viewAllWithCategories, viewBWithCategories, viewAWithCategories),
            db.dictionaryViews().getAll(dictionaryA.id).first()
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnId(): Unit = runBlockingTest {
        val faultyView = DictionaryViewEntity(
            id = "view_all",
            dictionaryId = dictionaryA.id,
            name = "View"
        )
        db.dictionaryViews().insertViews(listOf(faultyView))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnDictionaryId(): Unit = runBlockingTest {
        val faultyView = DictionaryViewEntity(
            id = "view",
            dictionaryId = "garbage",
            name = "View"
        )
        db.dictionaryViews().insertViews(listOf(faultyView))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnDictionaryViewId(): Unit = runBlockingTest {
        val faultyRelation = DictionaryViewToCategory(
            id = "garbage",
            categoryId = categoryA.id,
            dictionaryId = dictionaryA.id
        )
        db.dictionaryViews().insertRelation(listOf(faultyRelation))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnCategoryId(): Unit = runBlockingTest {
        val faultyRelation = DictionaryViewToCategory(
            id = viewAll.id,
            categoryId = "garbage",
            dictionaryId = dictionaryA.id
        )
        db.dictionaryViews().insertRelation(listOf(faultyRelation))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failRelationOnDictionaryId(): Unit = runBlockingTest {
        val faultyRelation = DictionaryViewToCategory(
            id = viewAll.id,
            categoryId = categoryA.id,
            dictionaryId = "garbage"
        )
        db.dictionaryViews().insertRelation(listOf(faultyRelation))
    }

    @Test
    fun removeViews(): Unit = runBlockingTest {
        db.dictionaryViews().removeRelations(dictionaryA.id)
        db.dictionaryViews().removeViews(dictionaryA.id)
        Assert.assertEquals(
            emptyList<DictionaryViewWithCategories>(),
            db.dictionaryViews().getAll(dictionaryA.id).first()
        )
    }

    @Test
    fun findView(): Unit = runBlockingTest {
        val viewAllWithCategories = DictionaryViewWithCategories(
            dictionaryView = viewAll,
            categories = listOf(categoryA, categoryB)
        )
        Assert.assertEquals(
            viewAllWithCategories,
            db.dictionaryViews().findById(dictionaryA.id, viewAll.id)
        )
        Assert.assertNull(db.dictionaryViews().findById(dictionaryA.id, "garbage"))
        Assert.assertNull(db.dictionaryViews().findById("garbage", viewAll.id))
    }
}
