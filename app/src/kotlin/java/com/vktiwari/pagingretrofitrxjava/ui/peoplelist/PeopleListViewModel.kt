package com.vktiwari.pagingretrofitrxjava.ui.peoplelist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.util.Log
import com.vktiwari.pagingretrofitrxjava.datasource.PeopleDataSourceFactory
import com.vktiwari.pagingretrofitrxjava.model.People
import com.vktiwari.pagingretrofitrxjava.network.NetworkState
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PeopleListViewModel(sourceFactory: DataSource.Factory<*, *>) : ViewModel() {
    private val TAG = "PeopleListViewModel"
    private var executor: Executor? = null
    private var peopleLiveData: LiveData<PagedList<People>>? = null
    private var networkStateLiveData: LiveData<NetworkState>? = null
    private var initialLoadingLiveData: LiveData<NetworkState>? = null
    private val pageSize = 40
    private var sourceFactory: PeopleDataSourceFactory = sourceFactory as PeopleDataSourceFactory;

    init {
        setup()
    }

    private fun setup() {
        executor = Executors.newFixedThreadPool(5)

        val config = PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize * 2)
                .setEnablePlaceholders(false)
                .build()
        peopleLiveData = LivePagedListBuilder(sourceFactory, config)
                .setFetchExecutor(executor!!)
                .build()

        networkStateLiveData = Transformations.switchMap(sourceFactory.getMutableLiveData()) { it.getNetworkState() }
        initialLoadingLiveData = Transformations.switchMap(sourceFactory.getMutableLiveData()) { it.getInitialLoading() }

    }

    fun getNetworkState(): LiveData<NetworkState>? {
        return networkStateLiveData
    }

    fun getInitialLoading(): LiveData<NetworkState>? {
        return initialLoadingLiveData
    }

    fun getPeopleLiveData(): LiveData<PagedList<People>>? {
        return peopleLiveData
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
        sourceFactory.getMutableLiveData().value!!.clear()
    }


    fun retryPagination() {
        sourceFactory.getMutableLiveData().value!!.retryPagination()
    }

    fun refresh() {
        sourceFactory.getMutableLiveData().value!!.invalidate()
    }
}