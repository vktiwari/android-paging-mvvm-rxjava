package com.vktiwari.pagingretrofitrxjava.datasource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.ItemKeyedDataSource
import android.util.Log
import com.vktiwari.pagingretrofitrxjava.model.People
import com.vktiwari.pagingretrofitrxjava.model.StarWarResponse
import com.vktiwari.pagingretrofitrxjava.network.NetworkService
import com.vktiwari.pagingretrofitrxjava.network.NetworkState
import com.vktiwari.pagingretrofitrxjava.network.StarWarRepo
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class PeopleDataSource : ItemKeyedDataSource<Int, People>() {

    private val networkState: MutableLiveData<NetworkState>
    private val initialLoading: MutableLiveData<NetworkState>
    private val compositeDisposable: CompositeDisposable
    private var retryCompletable: Completable? = null
    private var pageNumber: Int = 1
    private val TAG = "PeopleDataSource"

    init {
        this.compositeDisposable = CompositeDisposable()
        this.networkState = MutableLiveData<NetworkState>()
        this.initialLoading = MutableLiveData<NetworkState>()
    }

    fun getNetworkState(): MutableLiveData<NetworkState> {
        return networkState
    }

    fun getInitialLoading(): MutableLiveData<NetworkState> {
        return initialLoading
    }

    fun clear() {
        pageNumber = 1
        compositeDisposable.dispose()
    }

    override fun loadInitial(params: ItemKeyedDataSource.LoadInitialParams<Int>, callback: ItemKeyedDataSource.LoadInitialCallback<People>) {
        Log.d("loadInitial", "params key=" + params.requestedInitialKey + " loadSize=" + params.requestedLoadSize)
        initialLoading.postValue(NetworkState.LOADING)
        networkState.postValue(NetworkState.LOADING)
        setRetry(Action { loadInitial(params, callback) })

        val peopleListDisposable = NetworkService.getInstance().getRetrofitService(StarWarRepo::class.java!!).getPeopleList(pageNumber)
                .timeout(30, TimeUnit.SECONDS)
                .subscribe({ starWarResponse ->
                    if (starWarResponse == null || starWarResponse!!.results == null) {
                        onInitialLoadError(Throwable("Something went wrong, try again"))
                    } else {
                        initialLoading.postValue(NetworkState.LOADED)
                        onResponseFetched(starWarResponse!!, callback)
                    }
                }, ({ this.onInitialLoadError(it) }))
        compositeDisposable.add(peopleListDisposable)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<People>) {

    }

    override fun loadAfter(params: ItemKeyedDataSource.LoadParams<Int>, callback: ItemKeyedDataSource.LoadCallback<People>) {
        Log.i("loadAfter", "Loading Rang " + params.key + " Count " + params.requestedLoadSize)

        networkState.postValue(NetworkState.LOADING)
        setRetry(Action { loadAfter(params, callback) })

        val peopleListDisposable = NetworkService.getInstance().getRetrofitService(StarWarRepo::class.java!!).getPeopleList(pageNumber)
                .timeout(30, TimeUnit.SECONDS)
                .subscribe({ starWarResponse ->
                    if (starWarResponse == null || starWarResponse!!.results == null) {
                        val throwable = Throwable("Something went wrong, try again")
                        onError(throwable)
                    } else {
                        onResponseFetched(starWarResponse!!, callback)
                    }
                }, ({ this.onError(it) }))
        compositeDisposable.add(peopleListDisposable)
    }

    private fun onInitialLoadError(throwable: Throwable) {
        val error = NetworkState.error(throwable.message)
        initialLoading.postValue(error)
        networkState.postValue(error)
    }

    private fun onError(throwable: Throwable) {
        val error = NetworkState.error(throwable.message)
        networkState.postValue(error)
    }

    private fun onResponseFetched(response: StarWarResponse<People>, callback: ItemKeyedDataSource.LoadCallback<People>) {
        Log.d(TAG, "onResponse fetched=$response")
        setRetry(null)
        if (response.hasMore()) {
            pageNumber = Integer.parseInt(response.next!!.split("page=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
        }
        callback.onResult(response.results!!)
        networkState.postValue(NetworkState.LOADED)
    }

    fun retryPagination() {
        if (retryCompletable != null) {
            val disposable = retryCompletable!!.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ Log.d("Retry", "subscribe") }, { throwable -> Log.e("Retry", "error" + throwable.message) })
            compositeDisposable.add(disposable)
        }
    }

    private fun setRetry(action: Action?) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }

    override fun getKey(item: People): Int {
        return pageNumber
    }
}