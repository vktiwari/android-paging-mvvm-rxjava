package com.vktiwari.pagingretrofitrxjava.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.vktiwari.pagingretrofitrxjava.model.People;
import com.vktiwari.pagingretrofitrxjava.model.StarWarResponse;
import com.vktiwari.pagingretrofitrxjava.network.NetworkService;
import com.vktiwari.pagingretrofitrxjava.network.NetworkState;
import com.vktiwari.pagingretrofitrxjava.network.StarWarAPI;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

public class PeopleDataSource extends ItemKeyedDataSource<Integer, People> {

    private final MutableLiveData<NetworkState> networkState;
    private final MutableLiveData<NetworkState> initialLoading;
    private final CompositeDisposable compositeDisposable;
    private Completable retryCompletable;
    private int pageNumber = 1;

    public PeopleDataSource() {
        this.compositeDisposable = new CompositeDisposable();
        this.networkState = new MutableLiveData<>();
        this.initialLoading = new MutableLiveData<>();
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return initialLoading;
    }

    public void clear() {
        pageNumber = 1;
        compositeDisposable.dispose();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<People> callback) {
        Log.d("loadInitial","params key="+params.requestedInitialKey+" loadSize="+params.requestedLoadSize);
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);
        setRetry(() -> loadInitial(params, callback));

        Disposable peopleListDisposable = NetworkService.getInstance().getRetrofitService(StarWarAPI.class).getPeopleList(pageNumber)
                .timeout(30, TimeUnit.SECONDS)
                .subscribe(starWarResponse -> {
                    if (starWarResponse == null || starWarResponse.results == null) {
                        onInitialLoadError(new Throwable("Something went wrong, try again"));
                    } else {
                        initialLoading.postValue(NetworkState.LOADED);
                        onResponseFetched(starWarResponse, callback);
                    }
                }, this::onInitialLoadError);
        compositeDisposable.add(peopleListDisposable);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<People> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<People> callback) {
        Log.i("loadAfter", "Loading Rang " + params.key + " Count " + params.requestedLoadSize);

        networkState.postValue(NetworkState.LOADING);
        setRetry(() -> loadAfter(params, callback));

        Disposable peopleListDisposable = NetworkService.getInstance().getRetrofitService(StarWarAPI.class).getPeopleList(pageNumber)
                .timeout(30, TimeUnit.SECONDS)
                .subscribe(starWarResponse -> {
                    if (starWarResponse == null || starWarResponse.results == null) {
                        Throwable throwable = new Throwable("Something went wrong, try again");
                        onError(throwable);
                    } else {
                        onResponseFetched(starWarResponse, callback);
                    }
                }, this::onError);
        compositeDisposable.add(peopleListDisposable);
    }

    private void onInitialLoadError(Throwable throwable) {
        NetworkState error = NetworkState.error(throwable.getMessage());
        initialLoading.postValue(error);
        networkState.postValue(error);
    }

    private void onError(Throwable throwable) {
        NetworkState error = NetworkState.error(throwable.getMessage());
        networkState.postValue(error);
    }

    private void onResponseFetched(StarWarResponse<People> response, LoadCallback<People> callback) {
        Log.d(TAG, "onResponse fetched=" + response);
        setRetry(null);
        if (response.hasMore()) {
            pageNumber = Integer.parseInt(response.next.split("page=")[1]);
        }
        callback.onResult(response.results);
        networkState.postValue(NetworkState.LOADED);
    }

    public void retryPagination() {
        if (retryCompletable != null) {
            Disposable disposable = retryCompletable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Log.d("Retry", "subscribe");
                    }, throwable -> Log.e("Retry", "error" + throwable.getMessage()));
            compositeDisposable.add(disposable);
        }
    }

    private void setRetry(final Action action) {
        if (action == null) {
            this.retryCompletable = null;
        } else {
            this.retryCompletable = Completable.fromAction(action);
        }
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull People item) {
        return pageNumber;
    }
}
