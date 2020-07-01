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
package com.github.cheapmon.balalaika.domain.misc

import android.os.Parcel
import android.os.Parcelable
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import java.lang.IllegalStateException

data class DictionaryParcel(
    val data: InstallState<Dictionary>
) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(data.t.dictionaryId)
        parcel.writeString(data.t.externalId)
        parcel.writeInt(data.t.version)
        parcel.writeString(data.t.name)
        parcel.writeString(data.t.summary)
        parcel.writeString(data.t.authors)
        parcel.writeString(data.t.additionalInfo)
        parcel.writeString(data.t.url)
        parcel.writeInt(if (data.t.isActive) 1 else 0)
        val state = when (data) {
            is InstallState.Installable -> 0
            is InstallState.Installed -> 1
            is InstallState.Updatable -> 2
        }
        parcel.writeInt(state)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DictionaryParcel> {
        override fun createFromParcel(parcel: Parcel): DictionaryParcel {
            val dictionary = Dictionary(
                dictionaryId = parcel.readLong(),
                externalId = parcel.readString() ?: "",
                version = parcel.readInt(),
                name = parcel.readString() ?: "",
                summary = parcel.readString() ?: "",
                authors = parcel.readString() ?: "",
                additionalInfo = parcel.readString() ?: "",
                url = parcel.readString() ?: "",
                isActive = parcel.readInt() == 1
            )
            val state = when (parcel.readInt()) {
                0 -> InstallState.Installable(dictionary)
                1 -> InstallState.Installed(dictionary)
                2 -> InstallState.Updatable(dictionary)
                else -> throw IllegalStateException()
            }
            return DictionaryParcel(state)
        }

        override fun newArray(size: Int): Array<DictionaryParcel?> {
            return arrayOfNulls(size)
        }
    }
}
