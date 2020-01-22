package com.github.cheapmon.balalaika

import com.github.cheapmon.balalaika.db.FullForm
import com.github.cheapmon.balalaika.db.LexemeProperty

data class ContextMenuEntry(
        val text: String,
        val action: () -> Unit
)

data class PropertyLine(
        val widget: String,
        val fullForm: FullForm,
        val category: String,
        val properties: List<LexemeProperty>
)

data class DictionaryEntry(
        val fullForm: FullForm,
        val lines: List<PropertyLine>
)
