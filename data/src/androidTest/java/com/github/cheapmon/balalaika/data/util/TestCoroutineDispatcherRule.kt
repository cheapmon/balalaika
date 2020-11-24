package com.github.cheapmon.balalaika.data.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.coroutines.ContinuationInterceptor

@OptIn(ExperimentalCoroutinesApi::class)
internal class TestCoroutineDispatcherRule : TestRule, TestCoroutineScope by TestCoroutineScope() {
    val dispatcher = coroutineContext[ContinuationInterceptor] as TestCoroutineDispatcher

    override fun apply(
        base: Statement,
        description: Description
    ): Statement = object : Statement() {
        override fun evaluate() {
            Dispatchers.setMain(dispatcher)
            base.evaluate()
            cleanupTestCoroutines()
            Dispatchers.resetMain()
        }
    }
}
