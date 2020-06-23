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
package com.github.cheapmon.balalaika.domain.resources

import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.db.entities.property.Property
import com.github.cheapmon.balalaika.db.entities.view.DictionaryView
import com.github.cheapmon.balalaika.domain.config.Config
import java.io.InputStream

/** Component which reads application resources */
interface ResourceLoader {
    /** [Category] file identifier */
    val categoriesId: Int

    /** [Lexeme] file identifier */
    val lexemesId: Int

    /** Full form file identifier */
    val fullFormsId: Int

    /** [Property] file identifier */
    val propertiesId: Int

    /** [DictionaryView] file identifier */
    val dictionaryViewsId: Int

    /** [Configuration][Config] file identifier */
    val configId: Int

    /** Read input file by identifier */
    fun read(id: Int): InputStream
}
