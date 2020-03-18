package com.github.cheapmon.balalaika.data.entities

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
    indices = [Index(value = ["category_id"]), Index(value = ["lexeme_id"])]
)
data class Property(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val propertyId: Long = 0,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "lexeme_id") val lexemeId: Long,
    @ColumnInfo(name = "value") val value: String
)

data class PropertyWithRelations(
    @Embedded val property: Property,
    @Relation(parentColumn = "category_id", entityColumn = "id") val category: Category,
    @Relation(parentColumn = "lexeme_id", entityColumn = "id") val lexeme: Lexeme
)

@Dao
interface PropertyDao {
    @Query("SELECT * FROM property")
    fun getAll(): Flow<List<Property>>

    @Transaction
    @Query("SELECT * FROM property")
    fun getAllWithRelations(): Flow<List<PropertyWithRelations>>

    @Transaction
    @Query("""SELECT property.id, property.category_id, property.lexeme_id, property.value 
                    FROM property JOIN category ON property.category_id = category.id
                    JOIN lexeme ON property.lexeme_id = lexeme.id
                    WHERE category.id IN (:categoryIds) AND category.hidden = 0""")
    fun getAllFiltered(categoryIds: List<Long>): Flow<List<PropertyWithRelations>>

    @Transaction
    @Query("""SELECT property.id, property.category_id, property.lexeme_id, property.value
                    FROM property JOIN category ON property.category_id = category.id
                    JOIN lexeme ON property.lexeme_id = lexeme.id
                    WHERE category.hidden = 0""")
    fun getAllVisible(): Flow<List<PropertyWithRelations>>

    @Transaction
    @Query("SELECT * FROM property WHERE value LIKE '%' || (:query) || '%'")
    fun findByValue(query: String): Flow<List<PropertyWithRelations>>

    @Transaction
    @Query("""SELECT property.id, property.category_id, property.lexeme_id, property.value
                    FROM property JOIN lexeme ON property.lexeme_id = lexeme.id
                    WHERE lexeme_id IN (SELECT DISTINCT lexeme_id FROM property 
                    WHERE category_id = (:categoryId) AND value = (:restriction))
                    AND (value LIKE '%' || (:query) || '%' OR form LIKE '%' || (:query) || '%')""")
    fun findByValueRestricted(
        query: String,
        categoryId: Long,
        restriction: String
    ): Flow<List<PropertyWithRelations>>

    @Query("SELECT COUNT(*) FROM property")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg properties: Property)
}