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
package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Property

/**
 * Widget for audio playback
 *
 * @see Property.Audio
 */
class AudioWidget(
    parent: ViewGroup,
    category: DataCategory,
    properties: List<Property.Audio>,
    hasActions: Boolean,
    menuListener: WidgetMenuListener,
    actionListener: WidgetActionListener<Property.Audio>
) : Widget<Property.Audio>(
    parent,
    category,
    properties,
    hasActions,
    menuListener,
    actionListener
) {
    override fun displayName(property: Property.Audio): String =
        property.name

    override fun actionIcon(property: Property.Audio): Int? =
        R.drawable.ic_audio

    // No meaningful context menu actions
    override fun createContextMenu(): AlertDialog? = null
    override val menuItems: Array<String> = arrayOf()
}
