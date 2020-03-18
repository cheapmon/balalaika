package com.github.cheapmon.balalaika.util

import android.content.Context
import com.github.cheapmon.balalaika.data.DB
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
object InjectorUtil {
    private fun provideDictionaryRepository(context: Context): DictionaryRepository {
        val db = DB.getInstance(context.applicationContext)
        return DictionaryRepository.getInstance(
            db.categories(),
            db.lexemes(),
            db.properties(),
            db.dictionaryViews()
        )
    }

    fun provideDictionaryViewModelFactory(context: Context): DictionaryViewModelFactory {
        val repository = provideDictionaryRepository(context)
        return DictionaryViewModelFactory(repository)
    }
}