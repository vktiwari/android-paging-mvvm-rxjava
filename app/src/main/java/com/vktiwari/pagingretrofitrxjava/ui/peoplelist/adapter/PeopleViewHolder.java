package com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vktiwari.pagingretrofitrxjava.R;
import com.vktiwari.pagingretrofitrxjava.model.People;

class PeopleViewHolder extends RecyclerView.ViewHolder {
    private TextView mTextView;
    private OnItemClickListener itemClickListener;

    private PeopleViewHolder(View view, OnItemClickListener itemClickListener) {
        super(view);
        this.itemClickListener = itemClickListener;
        mTextView = view.findViewById(R.id.text_name);
    }

    void bind(People people) {
        if (people != null) {
            mTextView.setText(people.name);
            mTextView.setOnClickListener(v -> itemClickListener.onClick(people));
        }

    }

    public static PeopleViewHolder create(ViewGroup parent, OnItemClickListener itemClickListener) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_people, parent, false);
        return new PeopleViewHolder(view, itemClickListener);
    }
}
