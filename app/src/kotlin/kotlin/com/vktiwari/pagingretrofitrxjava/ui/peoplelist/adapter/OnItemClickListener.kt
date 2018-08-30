package com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter

import com.vktiwari.pagingretrofitrxjava.model.People

interface OnItemClickListener {
    fun onClick(people: People)
}