package com.github.cheapmon.balalaika.util

import com.github.cheapmon.balalaika.model.*
import com.github.cheapmon.balalaika.model.Dictionary
import java.util.*

private val wordclass = DataCategory(
    id = "wordclass",
    name = "Wordclass",
    iconName = "ic_book",
    sequence = 0
)

private val transDe = DataCategory(
    id = "trans_de",
    name = "German translation",
    iconName = "ic_flag",
    sequence = 1
)

private val happy = DictionaryEntry(
    id = "happy",
    representation = "happy",
    base = null,
    properties = mapOf(
        wordclass to listOf(Property.Plain("adjective")),
        transDe to listOf(
            Property.Url("fröhlich", "https://www.dict.cc/deutsch-englisch/fr%C3%B6hlich.html"),
            Property.Url("glücklich", "https://www.dict.cc/deutsch-englisch/gl%C3%BCcklich.html")
        )
    ).toSortedMap { o1, o2 -> o1.sequence.compareTo(o2.sequence) },
    bookmark = Bookmark()
)

val happier = DictionaryEntry(
    id = "happier",
    representation = "happier",
    base = happy,
    properties = mapOf(
        wordclass to listOf(Property.Plain("adjective")),
        transDe to listOf(
            Property.Url(
                "glücklicher",
                "https://www.dict.cc/deutsch-englisch/gl%C3%BCcklicher.html"
            )
        )
    ).toSortedMap { o1, o2 -> o1.sequence.compareTo(o2.sequence) },
    bookmark = Bookmark()
)

private val garbage = DictionaryEntry(
    id = "garbage",
    representation = "garbage",
    base = null,
    properties = TreeMap(emptyMap()),
    bookmark = null
)

private val audio = DataCategory(
    id = "audio",
    name = "Audio",
    iconName = "ic_audio",
    sequence = 0
)

private val example = DataCategory(
    id = "example",
    name = "Example",
    iconName = "ic_book",
    sequence = 1
)

private val morphology = DataCategory(
    id = "morphology",
    name = "Morphology",
    iconName = "ic_view",
    sequence = 2
)

private val plain = DataCategory(
    id = "plain",
    name = "Plain",
    iconName = "ic_circle",
    sequence = 3
)

private val reference = DataCategory(
    id = "reference",
    name = "Reference",
    iconName = "ic_subject",
    sequence = 4
)

private val simple = DataCategory(
    id = "simple",
    name = "Simple",
    iconName = "ic_info",
    sequence = 5
)

private val url = DataCategory(
    id = "url",
    name = "Url",
    iconName = "ic_source",
    sequence = 6
)

private val wordnet = DataCategory(
    id = "wordnet",
    name = "Wordnet",
    iconName = "ic_flag",
    sequence = 7
)

val fullEntry = DictionaryEntry(
    id = "full_entry",
    representation = "Full entry",
    base = null,
    properties = mapOf(
        audio to listOf(
            Property.Audio(name = "File", fileName = "file"),
            Property.Audio(name = "File 2", fileName = "file2")
        ),
        example to listOf(
            Property.Example("This is an example", "This is an example"),
            Property.Example("This is an example", "This is an example")
        ),
        morphology to listOf(
            Property.Morphology(listOf("ex", "am", "ple"))
        ),
        plain to listOf(
            Property.Plain("Plain"), Property.Plain("example")
        ),
        reference to listOf(
            Property.Reference(happier),
            Property.Reference(happier)
        ),
        simple to listOf(
            Property.Simple("This"),
            Property.Simple("is"),
            Property.Simple("a"),
            Property.Simple("long"),
            Property.Simple("example"),
            Property.Simple("with"),
            Property.Simple("many"),
            Property.Simple("properties")
        ),
        url to listOf(
            Property.Url("example", "https://www.example.org"),
            Property.Url("example", "https://www.example.org")
        ),
        wordnet to listOf(
            Property.Wordnet("example", "https://www.example.org"),
            Property.Wordnet("example", "https://www.example.org")
        )
    ).toSortedMap { o1, o2 -> o1.sequence.compareTo(o2.sequence) },
    bookmark = Bookmark()
)

val sampleDictionaryEntries = listOf(happy, happier, garbage, fullEntry)

val sampleHistoryItems = listOf(
    HistoryItem(
        query = "happy",
        restriction = null
    ),
    HistoryItem(
        query = "happy",
        restriction = SearchRestriction(
            category = wordclass,
            text = "adjective"
        )
    )
)

val dictCC = Dictionary(
    id = "sample",
    version = 1,
    name = "Sample dictionary",
    authors = "Paul Hemetsberger",
    summary = "dict.cc is not only an online dictionary. It's an attempt to create a platform where users from all over the world can share their knowledge in the field of translations. Every visitor can suggest new translations and correct or confirm other users' suggestions. The challenging and most important part of the project is the so-called Contribute! system making this process possible. To guarantee that the users' work is not lost in case something happens to the maintainer of dict.cc (Paul Hemetsberger), the resulting vocabulary database can be, downloaded anytime.",
    additionalInfo = "https://www.dict.cc/?s=about%3A&l=e"
)

val sampleDictionaries: List<SimpleDictionary> = listOf(
    InstalledDictionary(
        dictionary = dictCC,
        isOpened = true
    ),
    InstalledDictionary(
        dictionary = dictCC.copy(id = "sample2"),
        isOpened = false
    ),
    DownloadableDictionary(
        dictionary = dictCC.copy(id = "sample3"),
        isInLibrary = false
    )
)
