package com.github.cheapmon.balalaika.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    indices = [
        Index(value = ["external_id"], unique = true),
        Index(value = ["sequence"], unique = true)
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val categoryId: Long,
    @ColumnInfo(name = "external_id") val externalId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "widget") val widget: WidgetType,
    @ColumnInfo(name = "sequence") val sequence: Int,
    @ColumnInfo(name = "hidden") val hidden: Boolean,
    @ColumnInfo(name = "order_by") val orderBy: Boolean
)

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM category")
    fun count(): Int

    @Insert
    fun insertAll(vararg categories: Category)
}
