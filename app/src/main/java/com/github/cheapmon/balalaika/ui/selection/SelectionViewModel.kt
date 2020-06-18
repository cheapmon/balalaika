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
package com.github.cheapmon.balalaika.ui.selection

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.cheapmon.balalaika.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flowOf

/**
 * View model for [SelectionListFragment]
 */
class SelectionViewModel @ViewModelInject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val _dictionaries = listOf(
        Dictionary(
            1,
            version = 3,
            name = "Dictionary A",
            summary = context.getString(R.string.impsum),
            additionalInfo = context.getString(R.string.impsum),
            authors = "Simon Kaleschke"
        ),
        Dictionary(
            2,
            version = 2,
            name = "Dictionary B",
            summary = "BBB",
            additionalInfo = "https://www.example.org is a very important website",
            authors = "Thomas the tank engine"
        )
    )

    /** All available local dictionaries */
    val dictionaries = flowOf(_dictionaries).asLiveData()
}