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

import android.graphics.text.LineBreaker
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.model.SearchRestriction

/** Adapters for the data binding library */
object BindingAdapters {
    /**
     * Enables setting the visibility of a view using a boolean
     *
     * Example:
     * ```xml
     * <data>
     *     <variable name="is_visible" type="Boolean" />
     * </data>
     *
     * <TextView
     *   android:visibility="@{is_visible}" />
     * ```
     */
    @JvmStatic
    @BindingAdapter("android:visibility")
    fun setVisibility(view: View, value: Boolean) {
        view.visibility = if (value) View.VISIBLE else View.GONE
    }

    /**
     * Justify text on newer devices
     *
     * Example:
     * ```xml
     * <TextView
     *   app:justify="@{true}" />
     * ```
     */
    @JvmStatic
    @BindingAdapter("justify")
    fun justify(view: TextView, value: Boolean) {
        if (value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.justificationMode =
                LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            view.justificationMode =
                LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }

    /**
     * Set the text of a TextView from a [SearchRestriction]
     *
     * Example:
     * ```xml
     * <data>
     *   <variable
     *     name="restriction"
     *     type="com.github.cheapmon.balalaika.model.SearchRestriction" />
     * </data>
     *
     * <TextView
     *   android:text="@{restriction}" />
     * ```
     */
    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(view: TextView, value: SearchRestriction?) {
        view.text = if (value == null) {
            view.resources.getString(R.string.no_restriction)
        } else {
            view.resources.getString(
                R.string.search_restriction,
                value.category.name,
                value.text
            )
        }
    }
}
