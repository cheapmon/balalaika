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
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.WidgetType
import com.github.cheapmon.balalaika.db.entities.property.PropertyWithCategory
import com.github.cheapmon.balalaika.util.ResourceUtil

/**
 * Widget for audio playback
 *
 * Property values are of the form `<description>;;;<file>`. The description can be arbitrary,
 * `<file>` must be a valid string identifier of an Android audio resource. For example,
 * `audio.wav` would have the value `audio`.
 *
 * @see WidgetType.AUDIO
 */
class AudioWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithCategory>,
    hasActions: Boolean,
    searchText: String?
) : BaseWidget(parent, listener, category, properties, hasActions, searchText) {
    override fun displayValue(value: String): String {
        return value.split(Regex(";;;")).firstOrNull() ?: ""
    }

    override fun actionIcon(value: String): Int? {
        // Ignore fields with no file reference
        value.split(Regex(";;;")).getOrNull(1) ?: return null
        return R.drawable.ic_audio
    }

    override fun onClickActionButtonListener(value: String) {
        val res = value.split(Regex(";;;")).getOrNull(1) ?: return
        listener.onClickAudioButton(ResourceUtil.raw(parent.context, res))
    }

    // No meaningful context menu actions
    override fun createContextMenu(): AlertDialog? = null
    override val menuItems: Array<String> = arrayOf()
    override val menuActions: List<() -> Unit> = listOf()
}
