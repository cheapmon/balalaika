package com.github.cheapmon.balalaika.data.result

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ResultTest {
    private lateinit var success: Result<String, Throwable>
    private lateinit var error: Result<String, Throwable>

    @Before
    fun init() = runBlockingTest {
        success = run<String> { "Test" }
        error = run<String> { throw IllegalStateException() }
    }

    @Test
    fun runsSuccessfully() = runBlockingTest {
        val success = success
        assertTrue(success is Result.Success && success.data == "Test")
    }

    @Test
    fun catchesError() = runBlockingTest {
        val error = error
        assertTrue(error is Result.Error && error.cause is IllegalStateException)
    }

    @Test
    fun or() = runBlockingTest {
        assertEquals("Test", success.or("Something else"))
        assertEquals("Something else", error.or("Something else"))
    }

    @Test
    fun mapsData() = runBlockingTest {
        val mappedSuccess = success.map { 1 }
        assertTrue(mappedSuccess is Result.Success && mappedSuccess.data == 1)
        val mappedError = error.map { 1 }
        assertTrue(mappedError is Result.Error && mappedError.cause is IllegalStateException)
    }

    @Test
    fun mapsError() = runBlockingTest {
        val mappedSuccess = success.mapError { 1 }
        assertTrue(mappedSuccess is Result.Success && mappedSuccess.data == "Test")
        val mappedError = error.mapError { 1 }
        assertTrue(mappedError is Result.Error && mappedError.cause == 1)
    }

    @Test
    fun runsOnSuccess() = runBlockingTest {
        var counter = 0
        success.onSuccess { counter++ }
        assertEquals(1, counter)
        error.onSuccess { counter++ }
        assertEquals(1, counter)
    }

    @Test
    fun runsOnError() = runBlockingTest {
        var counter = 0
        success.onError { counter++ }
        assertEquals(0, counter)
        error.onError { counter++ }
        assertEquals(1, counter)
    }

    private suspend fun <T> run(block: suspend () -> T): Result<T, Throwable> {
        return tryRun({}, block)
    }
}
