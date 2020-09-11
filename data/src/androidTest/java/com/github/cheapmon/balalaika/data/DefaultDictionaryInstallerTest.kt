package com.github.cheapmon.balalaika.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.cheapmon.balalaika.data.db.AppDatabase
import com.github.cheapmon.balalaika.data.repositories.dictionary.DictionaryDataSource
import com.github.cheapmon.balalaika.data.repositories.dictionary.LocalDictionaryDataSource
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.CsvEntityImporter
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.DefaultDictionaryInstaller
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.DictionaryInstaller
import com.github.cheapmon.balalaika.data.result.ProgressState
import com.github.cheapmon.balalaika.data.result.Result
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DefaultDictionaryInstallerTest {
    private lateinit var db: AppDatabase
    private lateinit var importer: CsvEntityImporter
    private lateinit var dataSource: DictionaryDataSource
    private lateinit var installer: DictionaryInstaller

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val dispatcher = TestCoroutineDispatcher()
    private val scope = TestCoroutineScope(dispatcher)

    @Before
    fun createDatabase() {
        Dispatchers.setMain(dispatcher)
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        importer = CsvEntityImporter(dispatcher, db)
        dataSource = LocalDictionaryDataSource(dispatcher, context, Moshi.Builder().build())
        installer = DefaultDictionaryInstaller(
            context,
            dispatcher,
            mapOf("LOCAL" to dataSource),
            importer,
            db
        )
    }

    @After
    fun closeDatabase() {
        Dispatchers.resetMain()
        scope.cleanupTestCoroutines()
        db.close()
    }

    @Test
    fun installsSampleDictionary() = runBlocking {
        // TODO: Use `scope.runBlockingTest`
        Assert.assertTrue(dataSource.hasDictionary("sample", 1))
        Assert.assertEquals(1, dataSource.getDictionaryList().size)
        val dictionary = dataSource.getDictionaryList().first()
        val progress = installer.addDictionaryToLibrary(DownloadableDictionary(dictionary)).toList()
        val result = progress.last()
        Assert.assertTrue(result is ProgressState.Finished && result.data is Result.Success)
        Assert.assertEquals(1, db.dictionaries().count())
        Assert.assertEquals(3, db.lexemes().count())
        Assert.assertEquals(0, context.filesDir.listFiles().size)
    }
}
