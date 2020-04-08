package com.github.cheapmon.balalaika.data.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "dictionary_view")
data class DictionaryView(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val dictionaryViewId: Long = 0,
    @ColumnInfo(name = "external_id") val externalId: String,
    @ColumnInfo(name = "name") val name: String
)

@Entity(
    tableName = "dictionary_view_to_category",
    primaryKeys = ["dictionary_view_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = DictionaryView::class,
            parentColumns = ["id"],
            childColumns = ["dictionary_view_id"]
        ),
        ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])
    ],
    indices = [Index(value = ["dictionary_view_id"]), Index(value = ["category_id"])]
)
data class DictionaryViewToCategory(
    @ColumnInfo(name = "dictionary_view_id") val dictionaryViewId: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long
)

data class DictionaryViewWithCategories(
    @Embedded val dictionaryView: DictionaryView,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            DictionaryViewToCategory::class,
            parentColumn = "dictionary_view_id",
            entityColumn = "category_id"
        )
    )
    val categories: List<Category>
)

@Dao
interface DictionaryViewDao {
    @Query("SELECT * FROM dictionary_view")
    fun getAll(): Flow<List<DictionaryView>>

    @Transaction
    @Query("SELECT * FROM dictionary_view")
    fun getAllWithCategories(): Flow<List<DictionaryViewWithCategories>>

    @Query("""SELECT category_id FROM dictionary_view_to_category 
                    WHERE dictionary_view_id = (:id)""")
    fun findCategoriesById(id: Long): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM dictionary_view")
    fun count(): Flow<Int>

    @Insert
    suspend fun insertAll(vararg dictionaryViews: DictionaryView)

    @Insert
    suspend fun insertAll(vararg dictionaryViewToCategory: DictionaryViewToCategory)
}
