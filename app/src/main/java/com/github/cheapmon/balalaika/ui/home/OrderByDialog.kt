package com.github.cheapmon.balalaika.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.Category
import java.lang.ClassCastException

class OrderByDialog(private val categories: Array<Category>) : DialogFragment() {
    private lateinit var listener: OrderByDialogListener

    interface OrderByDialogListener {
        fun onDialogItemClick(dialog: DialogFragment, category: Category)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setNegativeButton(R.string.cancel, null)
                    .setTitle(R.string.order_by)
                    .setItems(categories.map { it.name }.toTypedArray()) { _, which ->
                        listener.onDialogItemClick(this, categories[which])
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .create()
        } ?: throw IllegalStateException("Activity can't be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OrderByDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OrderByDialogListener!")
        }
    }
}