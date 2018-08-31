package com.vktiwari.pagingretrofitrxjava.model;

import android.os.Parcel;
import android.os.Parcelable;

class People : Parcelable {

    var id: Long = 0
    var name: String
    var height: String
    var mass: String
    var created: String

    init {
        id = (++increment).toLong()
    }

    constructor(`in`: Parcel) {
        name = `in`.readString()
        height = `in`.readString()
        mass = `in`.readString()
        created = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(height)
        dest.writeString(mass)
        dest.writeString(created)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this)
            return true

        val people = obj as People?
        return people!!.id == this.id
    }

    override fun toString(): String {
        return "People{" +
                "id=" + id +
                ", name='" + name + '\''.toString() +
                ", height='" + height + '\''.toString() +
                ", mass='" + mass + '\''.toString() +
                ", created='" + created + '\''.toString() +
                '}'.toString()
    }

    override fun hashCode(): Int {
        var result = increment
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + mass.hashCode()
        result = 31 * result + created.hashCode()
        return result
    }

    companion object {
        private var increment = 0
        @JvmField
        val CREATOR = object : Parcelable.Creator<People> {
            override fun createFromParcel(parcel: Parcel): People {
                return People(parcel)
            }

            override fun newArray(size: Int): Array<People?> {
                return arrayOfNulls(size)
            }
        }
    }
}
