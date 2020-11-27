package com.github.cheapmon.balalaika.data.result

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ProgressStateTest {
    @Test
    fun runsSuccessfully() = runBlockingTest {
        val progress: List<ProgressState<Int, String, Throwable>> = tryProgress({}) {
            for (n in 1..5) {
                progress(ProgressState.InProgress(n * 20, "Step $n"))
            }
            return@tryProgress 1
        }.toList()
        assertEquals(ProgressState.Init, progress[0])
        assertEquals(ProgressState.InProgress(0, null), progress[1])
        assertEquals(ProgressState.InProgress(20, "Step 1"), progress[2])
        assertEquals(ProgressState.InProgress(40, "Step 2"), progress[3])
        assertEquals(ProgressState.InProgress(60, "Step 3"), progress[4])
        assertEquals(ProgressState.InProgress(80, "Step 4"), progress[5])
        assertEquals(ProgressState.InProgress(100, "Step 5"), progress[6])
        assertEquals(ProgressState.Finished(Result.Success<Int, Throwable>(1)), progress[7])
    }


    @Test
    fun catchesError() = runBlockingTest {
        val progress: List<ProgressState<Int, String, Throwable>> = tryProgress({}) {
            for (n in 1..5) {
                progress(ProgressState.InProgress(n * 20, "Step $n"))
                if (n == 3) throw IllegalStateException()
            }
            return@tryProgress 1
        }.toList()
        assertEquals(ProgressState.Init, progress[0])
        assertEquals(ProgressState.InProgress(0, null), progress[1])
        assertEquals(ProgressState.InProgress(20, "Step 1"), progress[2])
        assertEquals(ProgressState.InProgress(40, "Step 2"), progress[3])
        assertEquals(ProgressState.InProgress(60, "Step 3"), progress[4])
        val error = progress[5]
        assertTrue(((error as ProgressState.Finished).data as Result.Error).cause is IllegalStateException)
    }
}
