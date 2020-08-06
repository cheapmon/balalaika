/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.db.entities.history

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.core.os.ParcelCompat
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.WidgetType
import com.github.cheapmon.balalaika.ui.history.HistoryFragment
import com.github.cheapmon.balalaika.ui.search.SearchFragment

/**
 * Optional restriction of a search query
 *
 * _Note_: This class is [parcelable][Parcelable] and can be passed between fragments.
 *
 * @see SearchFragment
 * @see HistoryFragment
 */
sealed class SearchRestriction : Parcelable {
    /** No additional search restriction */
    object None : SearchRestriction()

    /** A search restriction consisting of a [category][Category] and a [restriction] string */
    data class Some(
        /** [Category] */
        val category: Category,
        /** Restriction */
        val restriction: String
    ) : SearchRestriction()

    /** @suppress */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        when (this) {
            is None -> {
                ParcelCompat.writeBoolean(parcel, false)
            }
            is Some -> {
                ParcelCompat.writeBoolean(parcel, true)
                parcel.writeString(this.category.id)
                parcel.writeString(this.category.dictionaryId)
                parcel.writeString(this.category.name)
                parcel.writeString(this.category.widget.name)
                parcel.writeString(this.category.iconName)
                parcel.writeInt(this.category.sequence)
                ParcelCompat.writeBoolean(parcel, this.category.hidden)
                ParcelCompat.writeBoolean(parcel, this.category.orderBy)
                parcel.writeString(this.restriction)
            }
        }
    }

    /** @suppress */
    override fun describeContents(): Int {
        return 0
    }

    /** @suppress */
    @SuppressLint("ParcelCreator")
    companion object CREATOR : Parcelable.Creator<SearchRestriction> {
        override fun createFromParcel(parcel: Parcel): SearchRestriction {
            val isSome = ParcelCompat.readBoolean(parcel)
            return if (isSome) {
                Some(
                    category = Category(
                        id = parcel.readString()!!,
                        dictionaryId = parcel.readString()!!,
                        name = parcel.readString()!!,
                        widget = WidgetType.valueOf(parcel.readString()!!),
                        iconName = parcel.readString()!!,
                        sequence = parcel.readInt(),
                        hidden = ParcelCompat.readBoolean(parcel),
                        orderBy = ParcelCompat.readBoolean(parcel)
                    ),
                    restriction = parcel.readString()!!
                )
            } else {
                None
            }
        }

        override fun newArray(size: Int): Array<SearchRestriction?> {
            return arrayOfNulls(size)
        }
    }
}
