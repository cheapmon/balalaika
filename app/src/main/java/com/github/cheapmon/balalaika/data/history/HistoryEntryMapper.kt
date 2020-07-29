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
package com.github.cheapmon.balalaika.data.history

import com.github.cheapmon.balalaika.data.Mapper
import com.github.cheapmon.balalaika.db.entities.history.HistoryEntryWithCategory
import com.github.cheapmon.balalaika.db.entities.history.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
import javax.inject.Inject

class HistoryEntryMapper @Inject constructor() :
    Mapper<HistoryEntryWithCategory, HistoryEntryWithRestriction> {
    override fun map(value: HistoryEntryWithCategory): HistoryEntryWithRestriction {
        return if (value.category != null && value.historyEntry.restriction != null) {
            HistoryEntryWithRestriction(
                value.historyEntry,
                SearchRestriction.Some(value.category, value.historyEntry.restriction)
            )
        } else {
            HistoryEntryWithRestriction(
                value.historyEntry,
                SearchRestriction.None
            )
        }
    }
}
