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

import com.github.cheapmon.balalaika.data.db.history.HistoryItemWithCategory
import com.github.cheapmon.balalaika.model.HistoryItem
import com.github.cheapmon.balalaika.model.SearchRestriction
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Map from a [HistoryItemWithCategory] to a [HistoryItem]
 *
 * @property mapper Mapper for data categories
 */
@Singleton
internal class HistoryItemWithCategoryToHistoryItem @Inject constructor(
    private val mapper: CategoryEntityToDataCategory
) : Mapper<HistoryItemWithCategory, HistoryItem> {
    /** @suppress */
    override suspend operator fun invoke(from: HistoryItemWithCategory): HistoryItem {
        val searchRestriction =
            if (from.category != null && from.historyItem.restriction != null) {
                SearchRestriction(
                    category = mapper(from.category),
                    text = from.historyItem.restriction
                )
            } else null
        return HistoryItem(
            id = from.historyItem.id,
            query = from.historyItem.query,
            restriction = searchRestriction
        )
    }
}
