package com.github.cheapmon.balalaika.data.repositories

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.github.cheapmon.balalaika.data.db.FakeCacheEntryDao
import com.github.cheapmon.balalaika.data.db.FakeDictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.FakeDictionaryViewDao
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.mappers.FakeDictionaryEntryEntityToDictionaryEntryMapper
import com.github.cheapmon.balalaika.data.util.Constants
import com.github.cheapmon.balalaika.model.*
import com.github.cheapmon.balalaika.model.Dictionary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
internal class DictionaryEntryRepositoryTest {
    private val dispatcher = TestCoroutineDispatcher()

    private val dictionaryEntryDao = FakeDictionaryEntryDao()
    private val cacheEntryDao = FakeCacheEntryDao()
    private val dictionaryViewDao = FakeDictionaryViewDao()
    private val toDictionaryEntry = FakeDictionaryEntryEntityToDictionaryEntryMapper()

    private val repository = DictionaryEntryRepository(
        dictionaryEntryDao = dictionaryEntryDao,
        cacheEntryDao = cacheEntryDao,
        dictionaryViewDao = dictionaryViewDao,
        toDictionaryEntry = toDictionaryEntry
    )

    private val entityA = DictionaryEntryEntity(
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
    private val entryA = DictionaryEntry(
        id = "lex_a",
        representation = "Lexeme A",
        base = null,
        properties = TreeMap(),
        bookmark = null
    )
    private val entityB = DictionaryEntryEntity(
        lexeme = LexemeEntity(
            id = "lex_b",
            dictionaryId = "dic_a",
            form = "Lexeme B",
            baseId = null
        ),
        base = null,
        properties = emptyList(),
        bookmark = null
    )
    private val entryB = DictionaryEntry(
        id = "lex_b",
        representation = "Lexeme B",
        base = null,
        properties = TreeMap(),
        bookmark = null
    )
    private val dictionary = InstalledDictionary(
        dictionary = Dictionary("dic_a", 0, "Dictionary A", "", "", "")
    )
    private val viewEntity = DictionaryViewEntity(
        id = Constants.DEFAULT_DICTIONARY_VIEW_ID,
        name = "Default",
        dictionaryId = "dic_a"
    )
    private val view = DictionaryView(
        id = viewEntity.id,
        name = viewEntity.name,
        categories = emptyList()
    )
    private val category = DataCategory(
        id = Constants.DEFAULT_CATEGORY_ID,
        name = "Default",
        iconName = "ic_circle",
        sequence = 0
    )

    @After
    fun after() = dispatcher.runBlockingTest {
        dictionaryEntryDao.clear()
        cacheEntryDao.clear()
        dictionaryViewDao.clear()
    }

    @Test
    fun initiallyEmpty() = dispatcher.runBlockingTest {
        dictionaryViewDao.insertViews(listOf(viewEntity))
        val pagerFlow = repository.getDictionaryEntries(dictionary, view, category, null)
        val snapshotList = collectPagingItems(pagerFlow, dispatcher)
        assertTrue(snapshotList.items.isEmpty())
    }

    @Test
    fun getDictionaryEntries() = dispatcher.runBlockingTest {
        dictionaryViewDao.insertViews(listOf(viewEntity))
        dictionaryEntryDao.insert(entityA, entityB)
        val pagerFlow = repository.getDictionaryEntries(dictionary, view, category, null)
        val snapshotList = collectPagingItems(pagerFlow, dispatcher)
        assertEquals(listOf(entryA, entryB), snapshotList.items)
    }

    @Test
    fun queryDictionaryEntries() = dispatcher.runBlockingTest {
        dictionaryViewDao.insertViews(listOf(viewEntity))
        dictionaryEntryDao.insert(entityA, entityB)
        val pagerFlow = repository.queryDictionaryEntries(dictionary, "B", null)
        val snapshotList = collectPagingItems(pagerFlow, dispatcher)
        assertEquals(listOf(entryB), snapshotList.items)
    }

    @Test
    fun noEntriesFound() = dispatcher.runBlockingTest {
        dictionaryViewDao.insertViews(listOf(viewEntity))
        dictionaryEntryDao.insert(entityA, entityB)
        val pagerFlow = repository.queryDictionaryEntries(dictionary, "garbage", null)
        val snapshotList = collectPagingItems(pagerFlow, dispatcher)
        assertEquals(emptyList<DictionaryEntry>(), snapshotList.items)
    }

    private fun <T : Any> TestCoroutineScope.collectPagingItems(
        pagerFlow: Flow<PagingData<T>>,
        dispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
    ): ItemSnapshotList<T> {
        val differ = createSimpleDiffer<T>(dispatcher)
        val job = launch {
            pagerFlow.collect {
                differ.submitData(it)
            }
        }
        advanceUntilIdle()
        job.cancel()
        return differ.snapshot()
    }

    private fun <T : Any> createSimpleDiffer(dispatcher: CoroutineDispatcher): AsyncPagingDataDiffer<T> {
        return AsyncPagingDataDiffer(
            diffCallback = object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
                    oldItem == newItem
            },
            updateCallback = object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {}
                override fun onRemoved(position: Int, count: Int) {}
                override fun onMoved(fromPosition: Int, toPosition: Int) {}
                override fun onChanged(position: Int, count: Int, payload: Any?) {}
            },
            mainDispatcher = dispatcher,
            workerDispatcher = dispatcher
        )
    }
}
