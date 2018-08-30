package com.vktiwari.pagingretrofitrxjava.model;

import android.os.Parcel;
import android.os.Parcelable;

public class People implements Parcelable {
    private static int increment = 0;

    private long id;
    public String name;
    public String height;
    public String mass;
    public String created;

    public People(){
        id = ++increment;
    }

    public long getId() {
        return id;
    }

    protected People(Parcel in) {
        name = in.readString();
        height = in.readString();
        mass = in.readString();
        created = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(height);
        dest.writeString(mass);
        dest.writeString(created);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<People> CREATOR = new Creator<People>() {
        @Override
        public People createFromParcel(Parcel in) {
            return new People(in);
        }

        @Override
        public People[] newArray(int size) {
            return new People[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        People people = (People) obj;
        return people.id == this.id;
    }

    @Override
    public String toString() {
        return "People{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", height='" + height + '\'' +
                ", mass='" + mass + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}
