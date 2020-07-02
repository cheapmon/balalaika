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
package com.github.cheapmon.balalaika.domain.services

import com.github.cheapmon.balalaika.domain.misc.Config
import com.github.cheapmon.balalaika.domain.misc.Response
import com.github.cheapmon.balalaika.domain.resources.ResourceLoader
import javax.inject.Inject
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

class YamlDictionaryParser @Inject constructor(
    // TODO: Rename resource loader
    private val resourceLoader: ResourceLoader
) : DictionaryParser {
    override fun parse(): Response<Config> {
        return try {
            val yaml = Yaml(Constructor(Config::class.java))
            val contents = resourceLoader.dictionaryList
            val parsed = yaml.load(contents) as Config
            val data = parsed.copy(
                dictionaries = parsed.dictionaries.map {
                    it.copy(dictionaryId = 0, isActive = false)
                }
            )
            Response.Success(data)
        } catch (ex: Exception) {
            Response.Failure(ex)
        }
    }
}
