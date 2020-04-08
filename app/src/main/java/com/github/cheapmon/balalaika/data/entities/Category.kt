package com.github.cheapmon.balalaika.data.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    indices = [
        Index(value = ["external_id"], unique = true),
        Index(value = ["sequence"], unique = true)
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val categoryId: Long = 0,
    @ColumnInfo(name = "external_id") val externalId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "widget") val widget: WidgetType,
    @ColumnInfo(name = "icon_id") val iconId: String,
    @ColumnInfo(name = "sequence") val sequence: Int,
    @ColumnInfo(name = "hidden") val hidden: Boolean,
    @ColumnInfo(name = "order_by") val orderBy: Boolean
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM category")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg categories: Category)
}