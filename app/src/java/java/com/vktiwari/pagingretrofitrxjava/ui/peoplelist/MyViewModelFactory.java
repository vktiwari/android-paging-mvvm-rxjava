package com.vktiwari.pagingretrofitrxjava.ui.peoplelist;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.paging.DataSource;

public class MyViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private DataSource.Factory mParam;

    public MyViewModelFactory(DataSource.Factory param) {
        mParam = param;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new PeopleListViewModel(mParam);
    }
}