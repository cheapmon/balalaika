package com.github.cheapmon.balalaika.data.entities.view

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dictionary_view")
data class DictionaryView(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val dictionaryViewId: Long = 0,
    @ColumnInfo(name = "external_id") val externalId: String,
    @ColumnInfo(name = "name") val name: String
)
