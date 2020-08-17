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
package com.github.cheapmon.balalaika.data.db.category

/**
 * Types of Widgets
 *
 * Balalaika supports seven different widgets for variable purposes, from simple data presentation
 * (e.g. [PLAIN] or [KEY_VALUE]) to complex use cases like playback ([AUDIO]).
 */
internal enum class WidgetType {
    /** Widget for audio file playback */
    AUDIO,

    /** Widget for example sentences and long pieces of information */
    EXAMPLE,

    /** Widget for displaying a property category and value */
    KEY_VALUE,

    /** Widget for displaying morphological information */
    MORPHOLOGY,

    /** Widget for displaying a single value */
    PLAIN,

    /** Widget for showing references between dictionary entries */
    REFERENCE,

    /** Widget for enabling external links for additional information */
    URL,

    /** Widget for displaying Wordnet information */
    WORDNET
}
