package com.github.cheapmon.balalaika.ui

import com.github.cheapmon.balalaika.data.repositories.WordnetRepository
import com.github.cheapmon.balalaika.data.result.LoadState
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.model.WordnetInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface WordnetViewModel {
    val wordnetParam: Flow<Property.Wordnet?>

    fun setWordnetParam(property: Property.Wordnet?)
    fun getWordnetData(property: Property.Wordnet): Flow<LoadState<WordnetInfo, Throwable>>
}

class DefaultWordnetViewModel(private val wordnet: WordnetRepository) : WordnetViewModel {
    private val _wordnetParam: MutableStateFlow<Property.Wordnet?> = MutableStateFlow(null)
    override val wordnetParam: Flow<Property.Wordnet?> = _wordnetParam

    override fun setWordnetParam(property: Property.Wordnet?) {
        _wordnetParam.value = property
    }

    /** Load Wordnet information for a word */
    override fun getWordnetData(property: Property.Wordnet): Flow<LoadState<WordnetInfo, Throwable>> =
        wordnet.getWordnetData(property.url)
}
