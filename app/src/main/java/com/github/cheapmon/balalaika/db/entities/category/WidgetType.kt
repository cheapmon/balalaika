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
package com.github.cheapmon.balalaika.db.entities.category

import com.github.cheapmon.balalaika.db.entities.category.WidgetType.AUDIO
import com.github.cheapmon.balalaika.db.entities.category.WidgetType.KEY_VALUE
import com.github.cheapmon.balalaika.db.entities.category.WidgetType.PLAIN
import com.github.cheapmon.balalaika.db.entities.property.Property
import com.github.cheapmon.balalaika.ui.dictionary.widgets.AudioWidget
import com.github.cheapmon.balalaika.ui.dictionary.widgets.BaseWidget
import com.github.cheapmon.balalaika.ui.dictionary.widgets.ExampleWidget
import com.github.cheapmon.balalaika.ui.dictionary.widgets.MorphologyWidget
import com.github.cheapmon.balalaika.ui.dictionary.widgets.PlainWidget
import com.github.cheapmon.balalaika.ui.dictionary.widgets.ReferenceWidget
import com.github.cheapmon.balalaika.ui.dictionary.widgets.UrlWidget
import com.github.cheapmon.balalaika.ui.dictionary.widgets.Widget

/**
 * Types of Widgets
 *
 * Balalaika supports seven different widgets for variable purposes, from simple data presentation
 * (e.g. [PLAIN] or [KEY_VALUE]) to complex use cases like playback ([AUDIO]).
 *
 * @see Widget
 */
enum class WidgetType {
    /**
     * Widget for audio file playback
     *
     * @see AudioWidget
     */
    AUDIO,

    /**
     * Widget for example sentences and long pieces of information
     *
     * @see ExampleWidget
     */
    EXAMPLE,

    /**
     * Widget for displaying a property category ([Category.name]) and value ([Property.value])
     *
     * @see BaseWidget
     */
    KEY_VALUE,

    /**
     * Widget for displaying morphological information
     *
     * @see MorphologyWidget
     */
    MORPHOLOGY,

    /**
     * Widget for displaying a single value ([Property.value])
     *
     * @see PlainWidget
     */
    PLAIN,

    /**
     * Widget for showing references between dictionary entries
     *
     * @see ReferenceWidget
     */
    REFERENCE,

    /**
     * Widget for enabling external links for additional information
     *
     * @see UrlWidget
     */
    URL
}
