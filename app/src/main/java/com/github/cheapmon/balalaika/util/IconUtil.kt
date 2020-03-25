package com.github.cheapmon.balalaika.util

import android.content.Context

object IconUtil {
    private val idCache = hashMapOf<String, Int>()

    fun getIdentifier(context: Context, name: String): Int {
        val id = idCache[name]
        return if (id == null) {
            val newId = context.resources.getIdentifier(
                name,
                "drawable",
                "com.github.cheapmon.balalaika"
            )
            idCache[name] = newId
            newId
        } else {
            id
        }
    }
}