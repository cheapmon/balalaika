package com.github.cheapmon.balalaika.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        ),
        ForeignKey(entity = Lexeme::class, parentColumns = ["id"], childColumns = ["lexeme_id"])
    ],
    indices = [Index(value = ["category_id", "lexeme_id"])]
)
data class Property(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val propertyId: Long = 0,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "lexeme_id") val lexemeId: Long,
    @ColumnInfo(name = "value") val value: String
)

data class PropertyWithCategory(
    @Embedded val property: Property,
    @Relation(parentColumn = "category_id", entityColumn = "id") val category: Category
)

@Dao
interface PropertyDao {
    @Query("SELECT * FROM property")
    fun getAll(): Flow<List<Property>>

    @Transaction
    @Query("SELECT * FROM property")
    fun getAllWithCategories(): Flow<List<PropertyWithCategory>>

    @Query("SELECT COUNT(*) FROM property")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg properties: Property)
}