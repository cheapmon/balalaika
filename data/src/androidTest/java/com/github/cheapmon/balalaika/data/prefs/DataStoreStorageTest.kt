package com.github.cheapmon.balalaika.data.prefs

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class DataStoreStorageTest {
    @Test
    fun storesDictionaryIds() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = DataStoreStorage(context)
        storage.setOpenDictionary(null)
        var dictionary = storage.openDictionary.first()
        Assert.assertEquals(null, dictionary)
        storage.setOpenDictionary("dic_a")
        dictionary = storage.openDictionary.first()
        Assert.assertEquals("dic_a", dictionary)
        val list = mutableListOf<String?>()
        val boolList = mutableListOf<Boolean>()
        val job = launch {
            storage.openDictionary.collect { list.add(it) }
        }
        val secondJob = launch {
            storage.openDictionary.map { it == null }.collect { boolList.add(it) }
        }
        storage.setOpenDictionary("dic_b")
        storage.setOpenDictionary(null)
        job.cancel()
        secondJob.cancel()
        Assert.assertEquals(
            listOf("dic_a", "dic_b", null),
            list
        )
        Assert.assertEquals(
            listOf(false, false, true),
            boolList
        )
    }
}
