package com.github.cheapmon.balalaika.util

import android.content.Context

object ResourceUtil {
    private val idCache = hashMapOf<String, Int>()

    fun drawable(context: Context, name: String) = getIdentifier(context, name, "drawable")
    fun raw(context: Context, name: String) = getIdentifier(context, name, "raw")

    private fun getIdentifier(context: Context, name: String, defType: String): Int {
        val id = idCache[name]
        return if (id == null) {
            val newId = context.resources.getIdentifier(
                name,
                defType,
                "com.github.cheapmon.balalaika"
            )
            idCache[name] = newId
            newId
        } else {
            id
        }
    }
}