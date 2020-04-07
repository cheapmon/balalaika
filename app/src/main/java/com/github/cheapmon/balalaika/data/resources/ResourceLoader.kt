package com.github.cheapmon.balalaika.data.resources

import java.io.InputStream

interface ResourceLoader {
    val categoriesId: Int
    val lexemesId: Int
    val fullFormsId: Int
    val propertiesId: Int
    val dictionaryViewsId: Int
    val configId: Int

    fun read(id: Int): InputStream
}