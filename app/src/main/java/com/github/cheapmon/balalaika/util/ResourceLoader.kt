package com.github.cheapmon.balalaika.util

import android.content.Context
import com.github.cheapmon.balalaika.R
import java.io.InputStream

interface ResourceLoader {
    public val defaultCategoriesID: Int
    public val defaultLexemesID: Int
    public val defaultFullFormsID: Int
    public val defaultVersionID: Int
    public val defaultPropertiesID: Int
    public fun openCSV(resourceID: Int): InputStream
}

class AndroidResourceLoader(private val context: Context) : ResourceLoader {
    override val defaultCategoriesID: Int = R.raw.categories
    override val defaultLexemesID: Int = R.raw.lexemes
    override val defaultFullFormsID: Int = R.raw.full_forms
    override val defaultVersionID: Int = R.raw.db
    override val defaultPropertiesID: Int = R.raw.properties
    override fun openCSV(resourceID: Int): InputStream = this.context.resources.openRawResource(resourceID)
}