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
package com.github.cheapmon.balalaika.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Data category of a dictionary entry
 *
 * A dictionary might hold many different pieces of information, for example:
 * - Orthographic information
 * - Part of speech
 * - Morphology
 * - Example sentences
 * - Pronunciation of words
 *
 * Additionally, each piece of information might be displayed differently.
 * Since a lot of languages are only sparsely documented and might require very unique data
 * categories to represent information, we cannot possibly provide all data categories which might
 * be used in a dictionary context.
 *
 * Instead of data category _type_, we simply differentiate based on data category _presentation_.
 * This kind of differentiation enables a flexible and wide-spread language support, which is one
 * of the main goals of this project.
 *
 * @property id Unique identifier of this data category
 * @property name Display name of this data category
 * @property iconName File name of the icon for this property used in the user interface
 * @property sequence Order in which the data categories are shown
 *
 * @see Property
 */
@Parcelize
data class DataCategory(
    val id: String,
    val name: String,
    val iconName: String,
    val sequence: Int
) : Parcelable
