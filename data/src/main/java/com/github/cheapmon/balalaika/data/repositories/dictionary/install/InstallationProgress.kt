package com.github.cheapmon.balalaika.data.repositories.dictionary.install

import com.github.cheapmon.balalaika.data.result.ProgressState
import kotlinx.coroutines.flow.Flow

internal typealias InstallationProgress = Flow<ProgressState<Unit, InstallationMessage, Throwable>>
