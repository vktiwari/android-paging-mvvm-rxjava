package com.vktiwari.pagingretrofitrxjava.datasource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.vktiwari.pagingretrofitrxjava.model.People

class PeopleDataSourceFactory : DataSource.Factory<Int,People>(){
    private var mutableLiveData: MutableLiveData<PeopleDataSource> = MutableLiveData()

    override fun create(): DataSource<Int, People> {
        val peopleDataSource = PeopleDataSource()
        mutableLiveData.postValue(peopleDataSource)
        return peopleDataSource
    }

    fun getMutableLiveData(): MutableLiveData<PeopleDataSource> {
        return mutableLiveData
    }
}