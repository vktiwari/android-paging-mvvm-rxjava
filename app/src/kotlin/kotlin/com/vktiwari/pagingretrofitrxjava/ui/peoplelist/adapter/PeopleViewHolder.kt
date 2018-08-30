package com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vktiwari.pagingretrofitrxjava.R
import com.vktiwari.pagingretrofitrxjava.model.People

class PeopleViewHolder(view: View, val itemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(view) {
    private var mTextView: TextView

    init {
        mTextView = view.findViewById(R.id.text_name)
    }

    fun bind(people: People?) {
        if (people != null) {
            mTextView.text = people.name
            mTextView.setOnClickListener { v -> itemClickListener.onClick(people) }
        }

    }

    companion object {
        fun create(parent: ViewGroup, itemClickListener: OnItemClickListener): PeopleViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_people, parent, false)
            return PeopleViewHolder(view, itemClickListener)
        }
    }

}