package com.github.cheapmon.balalaika.util

import com.github.cheapmon.balalaika.model.*
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

private val happier = DictionaryEntry(
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

val sampleDictionaryEntries = listOf(happy, happier, garbage)

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