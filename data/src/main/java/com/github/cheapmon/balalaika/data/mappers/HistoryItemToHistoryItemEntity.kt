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

import com.github.cheapmon.balalaika.data.db.history.HistoryItemEntity
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.HistoryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class HistoryItemToHistoryItemEntity @Inject constructor() {
    operator fun invoke(item: HistoryItem, dictionary: Dictionary): HistoryItemEntity =
        HistoryItemEntity(
            categoryId = item.restriction?.category?.id,
            dictionaryId = dictionary.id,
            restriction = item.restriction?.text,
            query = item.query
        )
}