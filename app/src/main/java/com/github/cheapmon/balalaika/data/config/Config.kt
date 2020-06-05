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
package com.github.cheapmon.balalaika.data.config

import androidx.room.Database
import com.github.cheapmon.balalaika.data.AppDatabase
import com.github.cheapmon.balalaika.data.config.Config.Source

/**
 * Configuration object which holds a version number and information about sources
 *
 * The configuration object is strictly read-only and has no additional behaviour.
 *
 * _Note_: [Config] and [Source] both define default values for their properties. This forces the
 * classes to have a default constructor, which is needed for deserialization.
 */
data class Config(
    /**
     * Version number of input files
     *
     * Instead of using the version indicator in [AppDatabase], the version number is given by some
     * input file.
     * These version numbers have a different meaning. [Database.version] indicates changes of the
     * database schema, whereas [version] is simply used for changes in the input files.
     */
    val version: Int = -1,
    /**
     * Sources of input files
     *
     * @see Source
     */
    val sources: List<Source> = listOf()
) {
    /** Input file source (e.g. from a corpus or a webservice) */
    data class Source(
        /** Name of the source **/
        val name: String? = null,
        /** List of source authors **/
        val authors: String? = null,
        /** Short description of the source */
        val summary: String? = null,
        /** External link to the source or its license */
        val url: String? = null
    )
}
