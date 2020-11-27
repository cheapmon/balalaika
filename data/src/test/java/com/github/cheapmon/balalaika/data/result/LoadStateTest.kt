package com.github.cheapmon.balalaika.data.result

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class LoadStateTest {
    private lateinit var success: List<LoadState<String, Throwable>>
    private lateinit var error: List<LoadState<String, Throwable>>

    @Before
    fun init() = runBlockingTest {
        success = load { "Test" }.toList()
        error = load<String> { throw IllegalStateException() }.toList()
    }

    @Test
    fun runsSuccessfully() = runBlockingTest {
        val result = success.result()
        assertTrue(result is Result.Success && result.data == "Test")
    }

    @Test
    fun catchesError() = runBlockingTest {
        val result = error.result()
        assertTrue(result is Result.Error && result.cause is IllegalStateException)
    }

    @Test
    fun or() = runBlockingTest {
        val successList = success.map { it.or("Something else") }
        assertEquals("Something else", successList[0])
        assertEquals("Something else", successList[1])
        assertEquals("Test", successList[2])
        val errorList = error.map { it.or("Something else") }
        assertEquals("Something else", errorList[0])
        assertEquals("Something else", errorList[1])
        assertEquals("Something else", errorList[2])
    }

    @Test
    fun mapsData() = runBlockingTest {
        val successResult = success.map { it.map { 1 } }.result()
        assertTrue(successResult is Result.Success && successResult.data == 1)
        val errorResult = error.map { it.map { 1 } }.result()
        assertTrue(errorResult is Result.Error && errorResult.cause is IllegalStateException)
    }

    @Test
    fun mapsError() = runBlockingTest {
        val successResult = success.map { it.mapError { 1 } }.result()
        assertTrue(successResult is Result.Success && successResult.data == "Test")
        val errorResult = error.map { it.mapError { 1 } }.result()
        assertTrue(errorResult is Result.Error && errorResult.cause == 1)
    }

    @Test
    fun runsOnSuccess() = runBlockingTest {
        var counter = 0
        success.forEach { it.onSuccess { counter++ } }
        assertEquals(1, counter)
        error.forEach { it.onSuccess { counter++ } }
        assertEquals(1, counter)
    }

    @Test
    fun runsOnError() = runBlockingTest {
        var counter = 0
        success.forEach { it.onError { counter++ } }
        assertEquals(0, counter)
        error.forEach { it.onError { counter++ } }
        assertEquals(1, counter)
    }

    private fun <T> load(block: suspend () -> T): Flow<LoadState<T, Throwable>> {
        return tryLoad({}, block)
    }

    private fun <T, E> List<LoadState<T, E>>.result(): Result<T, E> {
        assertTrue(this[0] is LoadState.Init)
        assertTrue(this[1] is LoadState.Loading)
        assertTrue(this[2] is LoadState.Finished)
        return (this.last() as LoadState.Finished).data
    }
}
