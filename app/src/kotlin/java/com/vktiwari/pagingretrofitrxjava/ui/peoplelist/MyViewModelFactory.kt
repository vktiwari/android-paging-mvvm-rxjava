package com.vktiwari.pagingretrofitrxjava.ui.peoplelist

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.paging.DataSource

class MyViewModelFactory(val param: DataSource.Factory<*, *>) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PeopleListViewModel(param) as T
    }
}