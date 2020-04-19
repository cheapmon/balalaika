package com.github.cheapmon.balalaika.data.storage

interface Storage {
    fun getInt(key: String, defValue: Int): Int
    fun getString(key: String, defValue: String?): String?

    fun putInt(key: String, value: Int)
    fun putString(key: String, value: String)

    fun contains(key: String): Boolean
}