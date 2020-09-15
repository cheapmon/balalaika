package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.mappers.RDFNodeToWordnetInfo
import com.github.cheapmon.balalaika.data.repositories.wordnet.WordnetApi
import com.github.cheapmon.balalaika.data.result.LoadState
import com.github.cheapmon.balalaika.data.result.tryLoad
import com.github.cheapmon.balalaika.model.WordnetInfo
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class WordnetRepository @Inject internal constructor(
    private val wordnetApi: WordnetApi,
    private val toWordnetInfo: RDFNodeToWordnetInfo
) {
    fun getWordnetData(url: String): Flow<LoadState<WordnetInfo, Throwable>> =
        tryLoad { wordnetApi.getWordnetData(url) }
            .map { loadState -> loadState.map { toWordnetInfo(it) } }
}
