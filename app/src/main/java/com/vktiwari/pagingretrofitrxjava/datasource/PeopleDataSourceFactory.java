package com.vktiwari.pagingretrofitrxjava.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.vktiwari.pagingretrofitrxjava.model.People;

public class PeopleDataSourceFactory extends DataSource.Factory<Integer, People> {
    private MutableLiveData<PeopleDataSource> mutableLiveData;

    public PeopleDataSourceFactory() {
        mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Integer, People> create() {
        PeopleDataSource peopleDataSource = new PeopleDataSource();
        mutableLiveData.postValue(peopleDataSource);
        return peopleDataSource;
    }

    public MutableLiveData<PeopleDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
