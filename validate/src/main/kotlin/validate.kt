/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import mu.KotlinLogging
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

object validate {
    private val logger = KotlinLogging.logger {}

    private lateinit var options: Options
    private lateinit var rootPath: Path

    private val WIDGET_TYPES = listOf(
            "audio",
            "example",
            "key_value",
            "morphology",
            "plain",
            "reference",
            "url"
    )
    private val BOOLEAN_TYPES = listOf(
            "0",
            "1"
    )
    private val ICON_TYPES = listOf(
            "ic_arrow_down",
            "ic_arrow_up",
            "ic_audio",
            "ic_bookmark",
            "ic_bookmark_border",
            "ic_circle",
            "ic_clear",
            "ic_comment",
            "ic_delete",
            "ic_filter",
            "ic_flag",
            "ic_forward",
            "ic_gavel",
            "ic_info",
            "ic_launcher_background",
            "ic_launcher_foreground",
            "ic_link",
            "ic_menu_book",
            "ic_menu_history",
            "ic_person",
            "ic_photo",
            "ic_preferences",
            "ic_redo",
            "ic_search",
            "ic_sort",
            "ic_source",
            "ic_subject",
            "ic_view"
    )

    private val cache: HashMap<String, ArrayList<String>> = hashMapOf(
            "categories" to arrayListOf(),
            "lexemes" to arrayListOf(),
            "views" to arrayListOf()
    )

    @JvmStatic
    fun main(args: Array<String>) {
        options = Options()
                .addOption("h", "help", false, "print this message")
                .addOption("p", "path", true, "path to files to check")
        val parser = try {
            DefaultParser().parse(options, args)
        } catch (ex: ParseException) {
            logger.error("Could not parse arguments ${args.joinToString(" ")}")
            printHelp(1)
        }
        if (parser.hasOption("help")) printHelp(0)

        val pathArg = parser.getOptionValue("path")
        rootPath = try {
            Paths.get(pathArg).toAbsolutePath().normalize()
        } catch (ex: Exception) {
            logger.error("Path $pathArg is invalid")
            printHelp(1)
        }
        logger.info("blk-validate v0.0")
        logger.info("Root path is $rootPath")

        checkFields("categories", "categories") { record ->
            listOfNotNull(
                    fieldIsNotEmpty(record, "id", "name", "widget", "icon", "sequence", "hidden", "order_by"),
                    fieldIsUnique(record, "categories", "id", "sequence"),
                    fieldIsWidget(record, "widget"),
                    fieldIsIcon(record, "icon"),
                    fieldIsBoolean(record, "hidden", "order_by")
            )
        }
        checkHeader("lexemes", "id", "form")
        checkFields("lexemes", "lexemes") { record ->
            listOfNotNull(
                    fieldIsNotEmpty(record, "id", "form"),
                    fieldIsUnique(record, "lexemes", "id")
            )
        }
        checkHeader("full_forms", "id", "form", "base")
        checkFields("full_forms", "lexemes") { record ->
            listOfNotNull(
                    fieldIsNotEmpty(record, "id", "form", "base"),
                    fieldIsUnique(record, "lexemes", "id"),
                    fieldIsReference(record, "lexemes", "base")
            )
        }
        checkFields("properties", "") { record ->
            listOfNotNull(
                    fieldIsNotEmpty(record, "id", "category", "value"),
                    fieldIsReference(record, "lexemes", "id"),
                    fieldIsReference(record, "categories", "category")
            )
        }
        checkHeader("views", "id", "name")
        checkFields("views", "") { record ->
            listOfNotNull(
                    fieldIsNotEmpty(record, "id", "name"),
                    fieldIsUnique(record, "views", "id")
            )
        }
    }

    private fun checkFields(fileName: String, cacheName: String, block: (CSVRecord) -> List<String>) {
        logger.info("Checking $fileName.csv fields")
        val records = readFile(rootPath.resolve("$fileName.csv"))
        var nProblems = 0
        records.forEachIndexed { index, record ->
            val msg = block(record)
            for (m in msg) logger.warn("Record ${index + 2}: $m")
            nProblems += msg.count()
            record.toMap()["id"]?.let { cache[cacheName]?.add(it) }
        }
        if (nProblems == 0) {
            logger.info("OK")
        } else {
            logger.warn("Found $nProblems problems")
        }
    }

    private fun checkHeader(fileName: String, vararg ignore: String) {
        logger.info("Checking $fileName.csv header")
        val ok = readFile(rootPath.resolve("$fileName.csv"))
                .first()
                .toMap()
                .keys
                .filterNot { ignore.contains(it) }
                .all { cache["categories"]!!.contains(it) }
        if (ok) {
            logger.info("OK")
        } else {
            logger.warn("Found faulty header")
        }
    }

    private fun fieldIsNotEmpty(record: CSVRecord, vararg field: String): String? {
        val fields = field.filter { record.toMap()[it].isNullOrBlank() }
        return if (fields.isEmpty()) null
        else "field(s) ${fields.joinToString()} must not be blank"
    }

    private fun fieldIsUnique(record: CSVRecord, table: String, vararg field: String): String? {
        val fields = field.filter { cache[table]!!.contains(record.toMap()[it]) }
        return if (fields.isEmpty()) null
        else "field(s) ${fields.joinToString()} are not unique"
    }

    private fun fieldIsWidget(record: CSVRecord, vararg field: String): String? {
        val fields = field.filter { !WIDGET_TYPES.contains(record.toMap()[it]) }
        return if (fields.isEmpty()) null
        else "field(s) ${fields.joinToString()} must be one of ${WIDGET_TYPES.joinToString()}"
    }

    private fun fieldIsBoolean(record: CSVRecord, vararg field: String): String? {
        val fields = field.filter { !BOOLEAN_TYPES.contains(record.toMap()[it]) }
        return if (fields.isEmpty()) null
        else "field(s) ${fields.joinToString()} must be one of ${BOOLEAN_TYPES.joinToString()}"
    }

    private fun fieldIsReference(
            record: CSVRecord,
            table: String,
            vararg field: String
    ): String? {
        val fields = field.filter { !cache[table]!!.contains(record.toMap()[it]) }
        return if (fields.isEmpty()) null
        else "field(s) ${fields.joinToString()} must be a correct foreign key"
    }

    private fun fieldIsIcon(record: CSVRecord, vararg field: String): String? {
        val fields = field.filter { !ICON_TYPES.contains(record.toMap()[it]) }
        return if (fields.isEmpty()) null
        else "field(s) ${field.joinToString()} must be one of ${ICON_TYPES.joinToString()}"
    }

    private fun readFile(path: Path): Iterable<CSVRecord> {
        try {
            return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(
                    InputStreamReader(Files.newInputStream(path))
            )
        } catch (ex: NoSuchFileException) {
            logger.error("Could not find ${path.fileName} in $rootPath")
            printHelp(1)
        } catch (ex: IOException) {
            logger.error("Could not parse ${path.fileName} in $rootPath")
            printHelp(1)
        }
    }

    private fun printHelp(status: Int): Nothing {
        HelpFormatter().printHelp("java -jar ./validate/build/libs/validate-0.0.jar [options]", options)
        exitProcess(status)
    }
}
