package com.vktiwari.pagingretrofitrxjava.ui.peoplelist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.vktiwari.pagingretrofitrxjava.datasource.PeopleDataSource;
import com.vktiwari.pagingretrofitrxjava.datasource.PeopleDataSourceFactory;
import com.vktiwari.pagingretrofitrxjava.model.People;
import com.vktiwari.pagingretrofitrxjava.network.NetworkState;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PeopleListViewModel extends ViewModel {
    private final String TAG = "PeopleListViewModel";
    private Executor executor;
    private LiveData<PagedList<People>> peopleLiveData;
    private LiveData<NetworkState> networkStateLiveData;
    private LiveData<NetworkState> initialLoadingLiveData;
    private int pageSize = 40;
    private PeopleDataSourceFactory sourceFactory;

    public PeopleListViewModel(DataSource.Factory sourceFactory) {
        this.sourceFactory = (PeopleDataSourceFactory) sourceFactory;
        setup();
    }

    private void setup() {
        executor = Executors.newFixedThreadPool(5);

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize * 2)
                .setEnablePlaceholders(false)
                .build();
        peopleLiveData = new LivePagedListBuilder<>(sourceFactory, config)
                .setFetchExecutor(executor)
                .build();

        networkStateLiveData = Transformations.switchMap(sourceFactory.getMutableLiveData(), PeopleDataSource::getNetworkState);
        initialLoadingLiveData = Transformations.switchMap(sourceFactory.getMutableLiveData(), PeopleDataSource::getInitialLoading);

    }

    public LiveData<NetworkState> getNetworkState() {
        return networkStateLiveData;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return initialLoadingLiveData;
    }

    public LiveData<PagedList<People>> getPeopleLiveData() {
        return peopleLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared");
        Objects.requireNonNull(sourceFactory.getMutableLiveData().getValue()).clear();
    }


    public void retryPagination() {
        Objects.requireNonNull(sourceFactory.getMutableLiveData().getValue()).retryPagination();
    }

    public void refresh() {
        Objects.requireNonNull(sourceFactory.getMutableLiveData().getValue()).invalidate();
    }
}
