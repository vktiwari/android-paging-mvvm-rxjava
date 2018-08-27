package com.vktiwari.pagingretrofitrxjava.datasource;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.vktiwari.pagingretrofitrxjava.model.People;

public class PeopleDataSourceFactory extends DataSource.Factory<Integer,People> {
    private MutableLiveData<PeopleDataSource> mutableLiveData;
    private PeopleDataSource peopleDataSource;

    public PeopleDataSourceFactory(){
        mutableLiveData = new MutableLiveData<PeopleDataSource>();
        peopleDataSource = new PeopleDataSource();
    }
    @Override
    public DataSource<Integer,People> create() {
        mutableLiveData.postValue(peopleDataSource);
        return peopleDataSource;
    }

    public MutableLiveData<PeopleDataSource> getMutableLiveData() {
        return mutableLiveData;
    }

    public PeopleDataSource getPeopleDataSource() {
        return peopleDataSource;
    }
}
