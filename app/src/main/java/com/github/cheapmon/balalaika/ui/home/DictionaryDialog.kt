package com.github.cheapmon.balalaika.ui.home

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.ui.widgets.ContextMenuEntry

class DictionaryDialog(
        private val title: String,
        private val entries: List<ContextMenuEntry>
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setNegativeButton(R.string.cancel, null)
                    .setTitle(title)
                    .setItems(entries.map { e -> e.text }.toTypedArray()) { _, which ->
                        entries[which]
                    }
                    .create()
        } ?: throw IllegalStateException("Activity can't be null")
    }
}