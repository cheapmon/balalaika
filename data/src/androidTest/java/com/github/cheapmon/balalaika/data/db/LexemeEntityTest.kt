package com.github.cheapmon.balalaika.data.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeWithBase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class LexemeEntityTest {
    private lateinit var db: AppDatabase

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private val dictionaryA = DictionaryEntity(
        id = "dic_a",
        version = 0,
        name = "Dictionary A",
        summary = "",
        authors = "",
        additionalInfo = ""
    )

    private val lexemeA = LexemeEntity(
        id = "lex_a",
        dictionaryId = "dic_a",
        form = "Lexeme A",
        baseId = null
    )

    private val lexemeB = LexemeEntity(
        id = "lex_b",
        dictionaryId = "dic_a",
        form = "Lexeme B",
        baseId = "lex_a"
    )

    @Before
    fun createDb(): Unit = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.dictionaries().insertAll(listOf(dictionaryA))
        db.lexemes().insertAll(listOf(lexemeA, lexemeB))
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getLexemes(): Unit = runBlockingTest {
        assertEquals(
            2,
            db.lexemes().count()
        )
    }

    @Test
    fun findLexeme(): Unit = runBlockingTest {
        assertEquals(
            LexemeWithBase(
                lexeme = lexemeB,
                base = lexemeA
            ),
            db.lexemes().getLexemeById(lexemeB.id)
        )
        assertNull(db.lexemes().getLexemeById("garbage"))
    }

    @Test
    fun insertLexeme(): Unit = runBlockingTest {
        val lexemeC = LexemeEntity(
            id = "lex_c",
            dictionaryId = "dic_a",
            form = "Lexeme C",
            baseId = null
        )
        db.lexemes().insertAll(listOf(lexemeC))
        assertEquals(
            3,
            db.lexemes().count()
        )
        assertEquals(
            listOf(lexemeA, lexemeB, lexemeC),
            db.lexemes().getAll()
        )
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnInsert(): Unit = runBlockingTest {
        db.lexemes().insertAll(listOf(lexemeA))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnFaultyForeignKey(): Unit = runBlockingTest {
        val faultyLexeme = LexemeEntity(
            id = "faulty",
            dictionaryId = "faulty",
            form = "faulty",
            baseId = null
        )
        db.lexemes().insertAll(listOf(faultyLexeme))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun failOnFaultyBaseId(): Unit = runBlockingTest {
        val faultyLexeme = LexemeEntity(
            id = "faulty",
            dictionaryId = "dic_a",
            form = "faulty",
            baseId = "garbage"
        )
        db.lexemes().insertAll(listOf(faultyLexeme))
    }

    @Test
    fun removeLexeme(): Unit = runBlockingTest {
        val dictionaryB = DictionaryEntity(
            id = "dic_b",
            version = 0,
            name = "Dictionary B",
            summary = "",
            authors = "",
            additionalInfo = ""
        )
        val lexemeC = LexemeEntity(
            id = "lex_c",
            dictionaryId = "dic_b",
            form = "Lexeme C",
            baseId = null
        )
        db.dictionaries().insertAll(listOf(dictionaryB))
        db.lexemes().insertAll(listOf(lexemeC))
        db.lexemes().removeInDictionary(dictionaryA.id)
        assertEquals(
            1,
            db.lexemes().count()
        )
        assertEquals(
            listOf(lexemeC),
            db.lexemes().getAll()
        )
        db.lexemes().removeInDictionary("garbage")
        assertEquals(
            1,
            db.lexemes().count()
        )
        assertEquals(
            listOf(lexemeC),
            db.lexemes().getAll()
        )
    }
}
