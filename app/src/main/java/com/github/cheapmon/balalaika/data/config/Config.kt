package com.github.cheapmon.balalaika.data.config

data class Config(
    val version: Int = -1,
    val sources: List<Source> = listOf()
) {
    data class Source(
        val name: String? = null,
        val authors: String? = null,
        val summary: String? = null,
        val url: String? = null
    )
}
