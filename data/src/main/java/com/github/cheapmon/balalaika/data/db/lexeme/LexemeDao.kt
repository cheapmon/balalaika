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
package com.github.cheapmon.balalaika.data.db.lexeme

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/** Database link for [lexemes][Lexeme] */
@Dao
internal interface LexemeDao {
    /** Find a [lexeme][Lexeme] by its [identifier][Lexeme.id] */
    @Transaction
    @Query("SELECT * FROM lexeme WHERE id = (:id) LIMIT 1")
    suspend fun getLexemeById(id: String): LexemeWithBase?

    /** Insert all [lexemes][Lexeme] into the database */
    @Insert
    suspend fun insertAll(lexemes: List<Lexeme>)

    /** Remove all lexemes associated with a dictionary */
    @Query("""DELETE FROM lexeme WHERE dictionary_id = (:dictionaryId)""")
    suspend fun removeInDictionary(dictionaryId: String)
}
