package com.github.cheapmon.balalaika.util

import android.content.Context
import com.github.cheapmon.balalaika.R
import java.io.InputStream

interface ResourceLoader {
    public val defaultCategoriesID: Int
    public val defaultLemmataID: Int
    public val defaultLexemesID: Int
    public val defaultVersionID: Int
    public fun openCSV(resourceID: Int): InputStream
}

class AndroidResourceLoader(private val context: Context) : ResourceLoader {
    override val defaultCategoriesID: Int = R.raw.categories
    override val defaultLemmataID: Int = R.raw.lemmata
    override val defaultLexemesID: Int = R.raw.lexemes
    override val defaultVersionID: Int = R.raw.db
    override fun openCSV(resourceID: Int): InputStream = this.context.resources.openRawResource(resourceID)
}