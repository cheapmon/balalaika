package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.FakeDictionaryDao
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntityToDictionary
import com.github.cheapmon.balalaika.data.prefs.FakePreferenceStorage
import com.github.cheapmon.balalaika.data.repositories.dictionary.FakeDictionaryDataSource
import com.github.cheapmon.balalaika.data.repositories.dictionary.FakeDictionaryInstaller
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DictionaryRepositoryTest {
    private val dispatcher = TestCoroutineDispatcher()

    private val dictionaryA = Dictionary("dic_a", 0, "Dictionary A", "", "", "")
    private val dictionaryB = Dictionary("dic_b", 0, "Dictionary B", "", "", "")

    private val storage = FakePreferenceStorage()
    private val dictionaryDao = FakeDictionaryDao()
    private val dataSources = mapOf(
        "FAKE" to FakeDictionaryDataSource(listOf(dictionaryA, dictionaryB))
    )
    private val installer = FakeDictionaryInstaller(dispatcher, dictionaryDao)
    private val toDictionary = DictionaryEntityToDictionary()

    private val repository = DictionaryRepository(
        storage = storage,
        dictionaryDao = dictionaryDao,
        dataSources = dataSources,
        installer = installer,
        toDictionary = toDictionary
    )


    @After
    fun after() = dispatcher.runBlockingTest {
        dictionaryDao.clear()
    }

    @Test
    fun noDictionary() = dispatcher.runBlockingTest {
        assertNull(repository.getOpenDictionary().first())
    }

    @Test
    fun openDictionary() = dispatcher.runBlockingTest {
        repository.addDictionaryToLibrary(dictionaryA).collect()
        repository.openDictionary(dictionaryA.installed())
        val result = repository.getOpenDictionary().first()
        assertEquals(dictionaryA.id, result?.id)
    }

    @Test
    fun multipleDictionaries() = dispatcher.runBlockingTest {
        repository.addDictionaryToLibrary(dictionaryA).collect()
        repository.addDictionaryToLibrary(dictionaryB).collect()
        repository.openDictionary(dictionaryA.installed())
        repository.openDictionary(dictionaryB.installed())
        val result = repository.getOpenDictionary().first()
        assertEquals(dictionaryB.id, result?.id)
        repository.closeDictionary()
        assertNull(repository.getOpenDictionary().first())
    }

    @Test
    fun getInstalledDictionaries() = dispatcher.runBlockingTest {
        repository.addDictionaryToLibrary(dictionaryA).collect()
        repository.addDictionaryToLibrary(dictionaryB).collect()
        repository.openDictionary(dictionaryA.installed())
        val result = repository
            .getInstalledDictionaries(dictionaryA.installed())
            .first()
            .map { it.id }
        assertEquals(listOf(dictionaryA.id, dictionaryB.id), result)
    }

    @Test
    fun getDownloadableDictionaries() = dispatcher.runBlockingTest {
        val result = repository.getDownloadableDictionaries().first().map { it.id }
        assertEquals(listOf(dictionaryA.id, dictionaryB.id), result)
    }

    private fun Dictionary.installed(isOpened: Boolean = false) =
        InstalledDictionary(this, isOpened)
}
