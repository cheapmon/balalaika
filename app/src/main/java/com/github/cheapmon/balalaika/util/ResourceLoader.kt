package com.github.cheapmon.balalaika.util

import android.content.Context
import com.github.cheapmon.balalaika.R
import java.io.InputStream

interface ResourceLoader {
    val defaultCategoriesID: Int
    val defaultLexemesID: Int
    val defaultFullFormsID: Int
    val defaultVersionID: Int
    val defaultPropertiesID: Int
    val defaultViewsID: Int
    fun openCSV(resourceID: Int): InputStream
}

class AndroidResourceLoader(private val context: Context) : ResourceLoader {
    override val defaultCategoriesID: Int = R.raw.categories
    override val defaultLexemesID: Int = R.raw.lexemes
    override val defaultFullFormsID: Int = R.raw.full_forms
    override val defaultVersionID: Int = R.raw.db
    override val defaultPropertiesID: Int = R.raw.properties
    override val defaultViewsID: Int = R.raw.views
    override fun openCSV(resourceID: Int): InputStream = this.context.resources.openRawResource(resourceID)
}