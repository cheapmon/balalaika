package com.github.cheapmon.balalaika.data.repositories.dictionary.install

import android.content.Context
import androidx.room.withTransaction
import com.github.cheapmon.balalaika.data.db.AppDatabase
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfig
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigWithRelations
import com.github.cheapmon.balalaika.data.di.IoDispatcher
import com.github.cheapmon.balalaika.data.repositories.dictionary.DictionaryDataSource
import com.github.cheapmon.balalaika.data.result.ProgressState
import com.github.cheapmon.balalaika.data.result.tryProgress
import com.github.cheapmon.balalaika.data.util.Constants
import com.github.cheapmon.balalaika.model.InstalledDictionary
import com.github.cheapmon.balalaika.model.SimpleDictionary
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.util.zip.ZipFile
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn

@Singleton
internal class DefaultDictionaryInstaller @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val dataSources: Map<String, @JvmSuppressWildcards DictionaryDataSource>,
    private val importer: CsvEntityImporter,
    private val db: AppDatabase
) : DictionaryInstaller {
    override fun addDictionaryToLibrary(
        dictionary: SimpleDictionary
    ): InstallationProgress = tryProgress {
        progress(ProgressState.InProgress(0, InstallationMessage.InstallCheckSources))
        val dataSource = findDataSource(dictionary)

        progress(ProgressState.InProgress(20, InstallationMessage.InstallDownloadZip))
        val tempFile = downloadZip(dataSource, dictionary)

        progress(ProgressState.InProgress(40, InstallationMessage.InstallExtractContents))
        val contents = extractZip(tempFile)

        progress(ProgressState.InProgress(60, InstallationMessage.InstallImportData))
        importer.import(dictionary, contents)

        progress(ProgressState.InProgress(80, InstallationMessage.InstallCleanup))
        tempFile.delete()

        progress(ProgressState.InProgress(100))
    }.flowOn(dispatcher)

    private suspend fun findDataSource(dictionary: SimpleDictionary): DictionaryDataSource =
        dataSources.values.find { it.hasDictionary(dictionary.id, dictionary.version) }
            ?: throw IllegalArgumentException("No datasource for this dictionary")

    private suspend fun downloadZip(
        dataSource: DictionaryDataSource,
        dictionary: SimpleDictionary
    ): File {
        val bytes = dataSource.getDictionaryContents(dictionary.id, dictionary.version)
        val tempFile = createTempFile("tmp", ".zip", context.filesDir)
        tempFile.outputStream().use { out -> out.write(bytes) }
        return tempFile
    }

    private fun extractZip(tempFile: File): DictionaryContents {
        val entries = ZipFile(tempFile).use { file ->
            file.entries().asSequence().map { entry ->
                Pair(
                    entry.name.removeSuffix(".csv"),
                    file.getInputStream(entry).bufferedReader().readText()
                )
            }.toMap()
        }
        return DictionaryContents(
            categories = entries["categories"] ?: missingFile("categories"),
            lexemes = entries["lexemes"] ?: missingFile("lexemes"),
            fullForms = entries["full_forms"] ?: missingFile("full_forms"),
            properties = entries["properties"] ?: missingFile("properties"),
            views = entries["views"] ?: missingFile("views")
        )
    }

    private fun missingFile(fileName: String): Nothing =
        throw FileNotFoundException("Could not find $fileName.csv")

    override fun removeDictionaryFromLibrary(
        dictionary: InstalledDictionary
    ): InstallationProgress = tryProgress {
        progress(ProgressState.InProgress(0, InstallationMessage.RemoveDeleteEntities))
        removeEntities(dictionary, update = false)

        progress(ProgressState.InProgress(50))
        db.dictionaries().remove(dictionary.id)

        progress(ProgressState.InProgress(100))
    }.flowOn(dispatcher)

    override fun updateDictionary(
        dictionary: InstalledDictionary
    ): InstallationProgress = tryProgress {
        progress(ProgressState.InProgress(0, InstallationMessage.UpdateDeleteEntities))
        val configuration = db.configurations().getConfigFor(dictionary.id).first()
        removeEntities(dictionary, update = true)

        progress(ProgressState.InProgress(33, InstallationMessage.UpdateInstall))
        addDictionaryToLibrary(dictionary).catch { throw it }.collect()

        progress(ProgressState.InProgress(66, InstallationMessage.UpdateCleanup))
        cleanupConfiguration(dictionary, configuration)

        progress(ProgressState.InProgress(100))
    }.flowOn(dispatcher)

    private suspend fun removeEntities(dictionary: SimpleDictionary, update: Boolean) =
        db.withTransaction {
            if (!update) {
                db.historyEntries().removeInDictionary(dictionary.id)
                db.configurations().removeConfigFor(dictionary.id)
                db.bookmarks().removeInDictionary(dictionary.id)
            }
            db.dictionaryViews().removeRelations(dictionary.id)
            db.dictionaryViews().removeViews(dictionary.id)
            db.properties().removeInDictionary(dictionary.id)
            db.lexemes().removeInDictionary(dictionary.id)
            db.categories().removeInDictionary(dictionary.id)
        }

    private suspend fun cleanupConfiguration(
        dictionary: SimpleDictionary,
        configuration: DictionaryConfigWithRelations?
    ) {
        var sortBy = configuration?.category?.id
        if (sortBy == null || db.categories().findById(dictionary.id, sortBy) == null) {
            sortBy = Constants.DEFAULT_CATEGORY_ID
        }
        var filterBy = configuration?.view?.dictionaryView?.id
        if (filterBy == null || db.dictionaryViews().findById(dictionary.id, filterBy) == null) {
            filterBy = Constants.DEFAULT_DICTIONARY_VIEW_ID
        }
        db.configurations().insert(DictionaryConfig(dictionary.id, sortBy, filterBy))
    }
}
