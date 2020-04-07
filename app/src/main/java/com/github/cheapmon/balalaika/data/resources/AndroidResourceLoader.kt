package com.github.cheapmon.balalaika.data.resources

import android.content.Context
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.resources.ResourceLoader
import java.io.InputStream
import javax.inject.Inject

class AndroidResourceLoader @Inject constructor(private val context: Context) :
    ResourceLoader {
    override val categoriesId: Int = R.raw.categories
    override val lexemesId: Int = R.raw.lexemes
    override val fullFormsId: Int = R.raw.full_forms
    override val propertiesId: Int = R.raw.properties
    override val dictionaryViewsId: Int = R.raw.views
    override val configId: Int = R.raw.config

    override fun read(id: Int): InputStream = context.resources.openRawResource(id)
}