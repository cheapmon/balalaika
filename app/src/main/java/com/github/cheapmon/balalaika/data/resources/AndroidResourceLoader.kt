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
package com.github.cheapmon.balalaika.data.resources

import android.content.Context
import com.github.cheapmon.balalaika.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.InputStream
import javax.inject.Inject

/**
 * [Resource loader][ResourceLoader] which reads Android resource files
 */
class AndroidResourceLoader @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceLoader {
    override val categoriesId: Int = R.raw.categories
    override val lexemesId: Int = R.raw.lexemes
    override val fullFormsId: Int = R.raw.full_forms
    override val propertiesId: Int = R.raw.properties
    override val dictionaryViewsId: Int = R.raw.views
    override val configId: Int = R.raw.config

    /** Read raw Android resource file */
    override fun read(id: Int): InputStream = context.resources.openRawResource(id)
}