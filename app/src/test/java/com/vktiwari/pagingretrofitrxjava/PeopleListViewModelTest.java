package com.vktiwari.pagingretrofitrxjava;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.paging.ItemKeyedDataSource;
import android.arch.paging.PagedList;

import com.vktiwari.pagingretrofitrxjava.datasource.PeopleDataSourceFactory;
import com.vktiwari.pagingretrofitrxjava.model.People;
import com.vktiwari.pagingretrofitrxjava.network.NetworkState;
import com.vktiwari.pagingretrofitrxjava.ui.peoplelist.PeopleListViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PeopleListViewModelTest {

    private PeopleListViewModel viewModel;
    private TestSubscriber<String> testSubscriber = TestSubscriber.create();
    private LifecycleOwner lifecycleOwner;
    private Lifecycle lifecycle;
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private PeopleDataSourceFactory sourceFactory;


    @Mock
    private ItemKeyedDataSource.LoadInitialCallback<People> initialLoadCallback;
    @Mock
    private ItemKeyedDataSource.LoadCallback<People> loadAfterCallback;

    private ItemKeyedDataSource.LoadInitialParams<Integer> initialLoadParams = new ItemKeyedDataSource.LoadInitialParams<>(1, 20, false);
    private ItemKeyedDataSource.LoadParams<Integer> loadAfterParams = new ItemKeyedDataSource.LoadParams<>(2, 20 * 2);

    private Observer<NetworkState> initialLoadObserver = networkState -> System.out.println("initialLoadObserver onChanged:networkState=" + networkState.getStatus());

    private Observer<NetworkState> networkStateObserver = networkState -> {
        System.out.println("networkStateObserver onChanged:networkState=" + networkState.getStatus());
        if (networkState.getStatus() == NetworkState.Status.FAILED) {
            removeViewModelObserver();
        }

    };

    private Observer<PagedList<People>> pagedListObserver = people -> System.out.println("pagedListObserver onChanged:data =" + people);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sourceFactory = new PeopleDataSourceFactory();
        sourceFactory.create();

        viewModel = new PeopleListViewModel(sourceFactory);
        lifecycleOwner = mock(LifecycleOwner.class);
        lifecycle = new LifecycleRegistry(lifecycleOwner);
        when(lifecycleOwner.getLifecycle()).thenReturn(lifecycle);
    }

    @Test
    public void test_networkState_change() {
        System.out.println("test_networkState_change");
        ((LifecycleRegistry) lifecycle).handleLifecycleEvent(Lifecycle.Event.ON_RESUME);

        viewModel.getNetworkState().observe(lifecycleOwner, networkState -> System.out.println("ViewModel OnChanged:networkState=" + networkState.getStatus()));

        sourceFactory.getMutableLiveData().getValue().getNetworkState().postValue(NetworkState.error("Error"));
        assertEquals("Error", viewModel.getNetworkState().getValue().getMessage());
        sourceFactory.getMutableLiveData().getValue().getNetworkState().postValue(NetworkState.LOADING);
        assertEquals(viewModel.getNetworkState().getValue().getStatus(), NetworkState.Status.RUNNING);
        sourceFactory.getMutableLiveData().getValue().getNetworkState().postValue(NetworkState.LOADED);
        assertEquals(viewModel.getNetworkState().getValue().getStatus(), NetworkState.Status.SUCCESS);

        ((LifecycleRegistry) lifecycle).handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        viewModel.getNetworkState().removeObservers(lifecycleOwner);
    }

    @Test
    public void test_loadInitial() {
        System.out.println("=============================test_loadInitial========================");
        addViewModelObserver();
        sourceFactory.getMutableLiveData().getValue().loadInitial(initialLoadParams, initialLoadCallback);
    }

    @Test
    public void test_loadAfter() {
        System.out.println("Note: loadInitial method should invoke first(as page library data source needed), so executing \"test_loadInitial\" first then test_loadAfter.");
        //It is mandatory that loadInitial should invoked before load after , so executing test_loadInitial herer first then loadAfter.
        test_loadInitial();
        System.out.println("============================test_loadAfter==========================");
        addViewModelObserver();
        sourceFactory.getMutableLiveData().getValue().loadAfter(loadAfterParams, loadAfterCallback);
    }

    private void addViewModelObserver() {
        System.out.println("Observer added into viewModel live data");
        if (!viewModel.getNetworkState().hasActiveObservers()) {
            viewModel.getNetworkState().observeForever(networkStateObserver);
        }
        if (!viewModel.getInitialLoading().hasActiveObservers()) {
            viewModel.getInitialLoading().observeForever(initialLoadObserver);
        }
        if (!viewModel.getPeopleLiveData().hasActiveObservers()) {
            viewModel.getPeopleLiveData().observeForever(pagedListObserver);
        }
    }

    private void removeViewModelObserver() {
        System.out.println("ViewModel observer has been removed ");
        if (viewModel.getNetworkState().hasActiveObservers()) {
            viewModel.getNetworkState().removeObserver(networkStateObserver);
        }
        if (viewModel.getInitialLoading().hasActiveObservers()) {
            viewModel.getInitialLoading().removeObserver(initialLoadObserver);
        }

        if (viewModel.getPeopleLiveData().hasActiveObservers()) {
            viewModel.getPeopleLiveData().removeObserver(pagedListObserver);
        }
    }

    @Test
    public void test_retryPagination(){
        System.out.println("===========test_retryPagination=============");
        viewModel.retryPagination();
    }

}
