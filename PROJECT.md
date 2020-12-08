# Project structure

## Modules

_Balalaika_ is written entirely in Kotlin and currently consists of **three**
modules:

- `model`: Data types used by the business logic in the whole application
- `data`: Database storage and persistence of dictionary data
- `app`: User interface and logic

## Libraries

Development relies heavily on official libraries and frameworks for the Android
ecosystem, e.g.:

- [Kotlin Coroutines][1]
- [Jetpack Compose][2]
- [Jetpack Navigation][3] (+ [SafeArgs Gradle plugin][4])
- [Jetpack Lifecycle Extensions][5]
- [Room persistence library][6]
- [Jetpack Paging 3][7]
- [Jetpack DataStore][8]
- [Dependency injection with Hilt][9]
- [Parcelize plugin][10]
- [JUnit + Mockito + Espresso][11] for testing

A lot of good introductory material can be found on the
[Android Developers YouTube Channel][12].

## Architecture

As per the [recommended Android app architecture][13] _Balalaika_ is structured
into a _Repository_ (`data` module), _ViewModel_ and _UI_ layer (`app` module).

Each repository exposes a simple API to persist dictionary and user data.
Usage of Kotlin's asynchronous [`Flow`][14] primitive ensures that changes are
seamlessly propagated to the user interface, consisting of small, flexible
components.

# Data model for Dictionary Data

_Balalaika_ provides a simple data model for saving and sending
dictionary data based on CSV. It is designed as a mediator format that can be
easily converted to from common dictionary data formats like [BantuLM][15].

Five different CSV tables are expected:

- `categories.csv`
- `lexemes.csv`
- `full_forms.csv`
- `properties.csv`
- `views.csv`

Remote and local datasources deliver these files compressed in the `.zip`
format.

## Data categories

_Data categories_ are flexible and differentiated based on their _visual
presentation_ (column `widget`).

- `id`: Unique identifier of this data category
- `name`: Display name of this data category
- `widget`: Visual presentation of this data category (`audio`, `example`,
  `morphology`, `plain`, `reference`, `key_value`, `url`, `wordnet`)
- `icon`: Display icon of this data category
- `sequence`: Order of data categories
- `hidden`: Data category should be visible in the UI
- `order_by`: Dictionary entries can be ordered by the data category

**Example `categories.csv`:**

```csv
id,name,widget,icon,sequence,hidden,order_by
trans_en,English Translation,url,ic_flag,1,0,1
wordclass,Wordclass,plain,ic_comment,0,0,1
morphology,Morphology,morphology,ic_subject,2,0,0
pronounciation,Pronounciation,audio,ic_audio,3,0,0
area,Area of Expertise,plain,ic_circle,4,1,0
```

## Lexemes and full forms

Dictionary entries are split into _lexemes_ and _full forms_. Lexemes are the
basic lexicographic unit of the dictionary and are described by their unique
identifier `id` and their orthographic representation `form`. Additional columns
will be interpreted as 1:1 properties of the lexeme, with a column header being
the `id` of a data category.

**Example `lexemes.csv`:**

```csv
id,form,wordclass,morphology,area
l_happy,happy,adjective,hap|py,mood
l_sad,sad,adjective,sad,mood
l_group,group,noun,group,relations
```

Full forms are mostly the same, expect for an additional column which describes
the lexeme the full form is based on.

**Example `full_forms.csv`:**

```csv
id,form,base,wordclass,morphology,area
f_happiness,happiness,l_happy,noun,happi|ness,mood
f_happier,happier,l_happy,adjective,happi|er,mood
f_happiest,happiest,l_happy,adjective,happi|est,mood
f_sadness,sadness,l_sad,noun,sad|ness,mood
f_groups,groups,l_group,noun,groups,relations
```

## 1:N properties

Additional properties of lexemes or full forms can be declared in a seperate
file.

**Example `properties.csv`:**

```csv
id,category,value
l_happy,trans_de,glücklich
l_happy,trans_de,fröhlich
f_happiness,trans_de,Glück
```

## Dictionary views

To satify different user needs, a number of _dictionary views_ can be defined.
They are described by their unique identifier `id` and their display `name`.
Additional columns are `id`s of data categories and denote if the category is
part of the dictionary view.

**Example `views.csv`:**

```csv
id,name,trans_en,wordclass,morphology
basic,Basic,1,1,0
trans_only,Translation only,1,0,0
```

## Dictionary list

Remote and local datasources designate their available datasources via a
`.json` file.

**Example `dictionaries.json`:**

```json
[
  {
    "name": "Sample dictionary",
    "id": "sample",
    "version": 1,
    "authors": "Paul Hemetsberger",
    "summary": "dict.cc is not only an online dictionary [...]",
    "additionalInfo": "https://www.dict.cc/?s=about%3A&l=e"
  }
]
```

[1]: https://kotlinlang.org/docs/coroutines-overview.html
[2]: https://developer.android.com/jetpack/compose
[3]: https://developer.android.com/guide/navigation
[4]: https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args
[5]: https://developer.android.com/topic/libraries/architecture/lifecycle
[6]: https://developer.android.com/topic/libraries/architecture/room
[7]: https://developer.android.com/topic/libraries/architecture/paging/v3-overview
[8]: https://developer.android.com/topic/libraries/architecture/datastore
[9]: https://developer.android.com/training/dependency-injection/hilt-android
[10]: https://developer.android.com/kotlin/parcelize
[11]: https://developer.android.com/training/testing/set-up-project
[12]: https://www.youtube.com/c/AndroidDevelopers/videos
[13]: https://developer.android.com/jetpack/guide
[14]: https://kotlinlang.org/docs/flow.html
[15]: https://github.com/MMoOn-Project/OpenBantu
