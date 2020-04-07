package com.github.cheapmon.balalaika.data.config

interface ConfigLoader {
    fun readConfig(): Config
}