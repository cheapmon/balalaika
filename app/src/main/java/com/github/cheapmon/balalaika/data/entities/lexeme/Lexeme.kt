package com.github.cheapmon.balalaika.data.entities.lexeme

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(entity = Lexeme::class, parentColumns = ["id"], childColumns = ["base_id"])
    ],
    indices = [
        Index(value = ["external_id"], unique = true),
        Index(value = ["base_id"])
    ]
)
data class Lexeme(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val lexemeId: Long = 0,
    @ColumnInfo(name = "external_id") val externalId: String,
    @ColumnInfo(name = "form") val form: String,
    @ColumnInfo(name = "base_id") val baseId: Long?,
    @ColumnInfo(name = "is_bookmark") val isBookmark: Boolean = false
)
