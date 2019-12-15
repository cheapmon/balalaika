package com.github.cheapmon.balalaika.util

import android.content.Context
import com.github.cheapmon.balalaika.R
import java.io.InputStream

interface ResourceLoader {
    public val defaultCategoriesID: Int
    public fun open(resourceID: Int): InputStream
}

class AndroidResourceLoader(private val context: Context) : ResourceLoader {
    override val defaultCategoriesID: Int = R.raw.categories
    override fun open(resourceID: Int): InputStream = this.context.resources.openRawResource(resourceID)
}