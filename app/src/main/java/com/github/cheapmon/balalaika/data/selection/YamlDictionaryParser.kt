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
package com.github.cheapmon.balalaika.data.selection

import com.github.cheapmon.balalaika.core.ListResponse
import com.github.cheapmon.balalaika.core.Response
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.di.DictionaryProviderType
import java.io.InputStream
import javax.inject.Inject
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

class YamlDictionaryParser @Inject constructor() {
    fun parse(contents: InputStream, providerKey: DictionaryProviderType?): ListResponse<Dictionary> {
        return try {
            val yaml = Yaml(Constructor(Config::class.java))
            val parsed = yaml.load(contents) as Config
            val data = parsed.dictionaries.map {
                it.copy(dictionaryId = 0, isActive = false).apply { this.providerKey = providerKey }
            }
            Response.Success(data)
        } catch (ex: Exception) {
            Response.Failure(ex)
        }
    }

    private data class Config(
        val dictionaries: List<Dictionary> = listOf()
    )
}
