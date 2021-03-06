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
package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.model.Dictionary
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Map from a [DictionaryEntity] to a [Dictionary]
 */
@Singleton
internal class DictionaryEntityToDictionary @Inject constructor() :
    Mapper<DictionaryEntity, Dictionary> {
    /** @suppress */
    override suspend operator fun invoke(from: DictionaryEntity): Dictionary = Dictionary(
        id = from.id,
        version = from.version,
        name = from.name,
        summary = from.summary,
        authors = from.authors,
        additionalInfo = from.additionalInfo
    )
}
