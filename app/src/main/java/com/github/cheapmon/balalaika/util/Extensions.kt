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
package com.github.cheapmon.balalaika.util

import android.content.Context
import android.os.Bundle
import android.text.SpannedString
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.entities.entry.PropertyDatabaseView
import com.github.cheapmon.balalaika.ui.search.SearchAdapter
import com.google.android.material.button.MaterialButton

/** Convenience method for setting the icon of a [MaterialButton] from a drawable ID */
fun MaterialButton.setIconById(@DrawableRes id: Int) {
    this.icon = ContextCompat.getDrawable(this.context, id)
}

/**
 * Highlight part of a string
 *
 * This is used in the user interface to indicate matching [dictionary entries][PropertyDatabaseView]
 * for a search query.
 *
 * @see SearchAdapter
 */
fun String.highlight(text: String?, context: Context): SpannedString {
    val contents = this
    return if (text == null || text == "" || !contents.contains(text)) {
        buildSpannedString { append(contents) }
    } else {
        val color = ContextCompat.getColor(context, R.color.mainColor)
        buildSpannedString {
            // Split before and after text
            contents.split(Regex("(?<=$text)|(?=$text)")).forEach {
                if (it == text) color(color) { append(it) }
                else append(it)
            }
        }
    }
}

/** Exhaustive matching for `when` in statements */
val <T> T.exhaustive: T
    get() = this

/** Logger for any class */
class Logger(private val name: String) {
    /** @suppress */
    fun assert(msg: Any?) = log(Log.ASSERT, msg)

    /** @suppress */
    fun debug(msg: Any?) = log(Log.DEBUG, msg)

    /** @suppress */
    fun error(msg: Any?) = log(Log.ERROR, msg)

    /** @suppress */
    fun info(msg: Any?) = log(Log.INFO, msg)

    /** @suppress */
    fun verbose(msg: Any?) = log(Log.VERBOSE, msg)

    /** @suppress */
    fun warn(msg: Any?) = log(Log.WARN, msg)
    private fun log(priority: Int, msg: Any?) = Log.println(priority, name, msg.toString())
}

/**
 * Access the [logger][Logger] for a class
 *
 * Sample usage:
 * ```
 * logger {
 *   info("Some information")
 *   debug(someValue)
 * }
 * ```
 */
inline fun <reified T> T.logger(block: Logger.() -> Unit) = Logger(T::class.java.name).block()

/**
 * [Lazy] delegate to access fragment arguments inside of a [ViewModel]
 *
 * _Note_: This is especially useful when using dependency injection, since Hilt can inject a
 * [SavedStateHandle], as explained in
 * [Hilt and Jetpack integrations](https://developer.android.com/training/dependency-injection/hilt-jetpack).
 *
 * Example:
 * ```
 * class MyViewModel @Inject constructor(
 *   @Assisted savedStateHandle: SavedStateHandle
 * ) : ViewModel() {
 *   private val navArgs: MyNavArgs by navArgs(savedStateHandle)
 * }
 * ```
 */
inline fun <reified Args : NavArgs> navArgs(savedStateHandle: SavedStateHandle) =
    NavArgsLazy(Args::class) { savedStateHandle.toBundle() }

/** Convert [SavedStateHandle] to [Bundle] */
fun SavedStateHandle.toBundle() =
    bundleOf(*this.keys().map { Pair(it, this.get<Any?>(it)) }.toTypedArray())
