package com.github.cheapmon.balalaika.data.entities.history

import androidx.room.Embedded
import androidx.room.Ignore

data class HistoryEntryWithRestriction(
    @Embedded val historyEntry: HistoryEntry,
    @Ignore val restriction: SearchRestriction
)