package com.vktiwari.pagingretrofitrxjava.ui.peoplelist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.vktiwari.pagingretrofitrxjava.datasource.PeopleDataSource;
import com.vktiwari.pagingretrofitrxjava.datasource.PeopleDataSourceFactory;
import com.vktiwari.pagingretrofitrxjava.model.People;
import com.vktiwari.pagingretrofitrxjava.network.NetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PeopleListViewModel extends ViewModel {
    private final String TAG = "PeopleListViewModel";
    private Executor executor;
    private LiveData<PagedList<People>> peopleLiveData;
    private int pageSize = 20;
    private PeopleDataSourceFactory sourceFactory;

    public PeopleListViewModel() {
        init();
    }

    private void init() {
        executor = Executors.newFixedThreadPool(5);
        sourceFactory = new PeopleDataSourceFactory();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize * 2)
                .setEnablePlaceholders(false)
                .build();
        peopleLiveData = new LivePagedListBuilder<>(sourceFactory, config)
                .setFetchExecutor(executor)
                .build();

    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(sourceFactory.getMutableLiveData(), PeopleDataSource::getNetworkState);

    }

    public LiveData<NetworkState> getInitialLoading() {
        return Transformations.switchMap(sourceFactory.getMutableLiveData(), PeopleDataSource::getInitialLoading);
    }

    LiveData<PagedList<People>> getPeopleLiveData() {
        return peopleLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        sourceFactory.getPeopleDataSource().clear();
        Log.d(TAG, "onCleared");
    }


    public void retryPagination() {
        sourceFactory.getMutableLiveData().getValue().retryPagination();
    }

    public void refresh() {
        sourceFactory.getMutableLiveData().getValue().invalidate();
    }
}
