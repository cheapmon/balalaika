package com.github.cheapmon.balalaika.data.repositories.dictionary.install

import android.content.Context
import com.github.cheapmon.balalaika.data.di.IoDispatcher
import com.github.cheapmon.balalaika.data.repositories.dictionary.DictionaryDataSource
import com.github.cheapmon.balalaika.data.result.ProgressState
import com.github.cheapmon.balalaika.data.result.tryProgress
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileNotFoundException
import java.util.zip.ZipFile
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
internal class DefaultDictionaryInstaller @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val dataSources: Map<String, @JvmSuppressWildcards DictionaryDataSource>,
    private val importer: CsvEntityImporter
) : DictionaryInstaller {
    override suspend fun addDictionaryToLibrary(
        dictionary: DownloadableDictionary
    ): InstallationProgress = tryProgress {
        withContext(dispatcher) {
            progress(ProgressState.InProgress(0, InstallationMessage.InstallCheckSources))
            val dataSource = dataSources.values
                .find { it.hasDictionary(dictionary.id, dictionary.version) }
                ?: throw IllegalArgumentException("No datasource for this dictionary")

            progress(ProgressState.InProgress(20, InstallationMessage.InstallDownloadZip))
            val bytes = dataSource.getDictionaryContents(dictionary.id, dictionary.version)
            val tempFile = createTempFile("tmp", ".zip", context.filesDir)
            tempFile.outputStream().use { out -> out.write(bytes) }

            progress(ProgressState.InProgress(40, InstallationMessage.InstallExtractContents))
            val entries = ZipFile(tempFile).use { file ->
                file.entries().asSequence().map { entry ->
                    Pair(
                        entry.name.removeSuffix(".csv"),
                        file.getInputStream(entry).bufferedReader().readText()
                    )
                }.toMap()
            }
            val contents = DictionaryContents(
                categories = entries["categories"] ?: missingFile("categories"),
                lexemes = entries["lexemes"] ?: missingFile("lexemes"),
                fullForms = entries["full_forms"] ?: missingFile("full_forms"),
                properties = entries["properties"] ?: missingFile("properties"),
                views = entries["views"] ?: missingFile("views")
            )

            progress(ProgressState.InProgress(60, InstallationMessage.InstallImportData))
            importer.import(dictionary.id, contents)

            progress(ProgressState.InProgress(80, InstallationMessage.InstallCleanup))
            tempFile.delete()

            progress(ProgressState.InProgress(100))
        }
    }

    private fun missingFile(fileName: String): Nothing =
        throw FileNotFoundException("Could not find $fileName.csv")

    override suspend fun removeDictionaryFromLibrary(
        dictionary: DownloadableDictionary
    ): InstallationProgress {
        TODO("Not yet implemented")
    }

    override suspend fun updateDictionary(
        dictionary: DownloadableDictionary
    ): InstallationProgress {
        TODO("Not yet implemented")
    }
}
