package com.vktiwari.pagingretrofitrxjava

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.arch.paging.ItemKeyedDataSource
import android.arch.paging.PagedList
import com.vktiwari.pagingretrofitrxjava.datasource.PeopleDataSourceFactory
import com.vktiwari.pagingretrofitrxjava.model.People
import com.vktiwari.pagingretrofitrxjava.network.NetworkState
import com.vktiwari.pagingretrofitrxjava.ui.peoplelist.PeopleListViewModel
import io.reactivex.subscribers.TestSubscriber
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class PeopleListViewModelTest {
    private var viewModel: PeopleListViewModel? = null
    private val testSubscriber = TestSubscriber.create<String>()
    private var lifecycleOwner: LifecycleOwner? = null
    private var lifecycle: Lifecycle? = null

    @get:Rule
    public var rule: TestRule = InstantTaskExecutorRule()
    private var sourceFactory: PeopleDataSourceFactory? = null


    @Mock
    private val initialLoadCallback: ItemKeyedDataSource.LoadInitialCallback<People>? = null
    @Mock
    private val loadAfterCallback: ItemKeyedDataSource.LoadCallback<People>? = null

    private val initialLoadParams = ItemKeyedDataSource.LoadInitialParams(1, 20, false)
    private val loadAfterParams = ItemKeyedDataSource.LoadParams(2, 20 * 2)

    private val initialLoadObserver = Observer<NetworkState> { networkState -> println("initialLoadObserver onChanged:networkState=" + networkState!!.getStatus()!!) }

    private val networkStateObserver = Observer<NetworkState> { networkState ->
        println("networkStateObserver onChanged:networkState=" + networkState!!.getStatus()!!)
        if (networkState!!.getStatus() === NetworkState.Status.FAILED) {
            removeViewModelObserver()
        }

    }

    private val pagedListObserver = Observer<PagedList<People>> { people -> println("pagedListObserver onChanged:data =$people") }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        sourceFactory = PeopleDataSourceFactory()
        sourceFactory!!.create()

        viewModel = PeopleListViewModel(sourceFactory!!)
        lifecycleOwner = mock<LifecycleOwner>(LifecycleOwner::class.java)
        lifecycle = LifecycleRegistry(lifecycleOwner!!)
        `when`<Lifecycle>(lifecycleOwner!!.lifecycle).thenReturn(lifecycle)
    }

    @Test
    fun test_networkState_change() {
        println("test_networkState_change")
        (lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        viewModel!!.getNetworkState()!!.observe(lifecycleOwner!!, Observer { networkState -> println("ViewModel OnChanged:networkState=" + networkState!!.getStatus()!!) })

        sourceFactory!!.getMutableLiveData().value!!.getNetworkState().postValue(NetworkState.error("Error"))
        assertEquals("Error", viewModel!!.getNetworkState()!!.value!!.getMessage())
        sourceFactory!!.getMutableLiveData().value!!.getNetworkState().postValue(NetworkState.LOADING)
        assertEquals(viewModel!!.getNetworkState()!!.value!!.getStatus(), NetworkState.Status.RUNNING)
        sourceFactory!!.getMutableLiveData().value!!.getNetworkState().postValue(NetworkState.LOADED)
        assertEquals(viewModel!!.getNetworkState()!!.value!!.getStatus(), NetworkState.Status.SUCCESS)

        (lifecycle as LifecycleRegistry).handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModel!!.getNetworkState()!!.removeObservers(lifecycleOwner!!)
    }

    @Test
    fun test_loadInitial() {
        println("=============================test_loadInitial========================")
        addViewModelObserver()
        sourceFactory!!.getMutableLiveData().value!!.loadInitial(initialLoadParams, initialLoadCallback!!)
    }

    @Test
    fun test_loadAfter() {
        println("Note: loadInitial method should invoke first(as page library data source needed), so executing \"test_loadInitial\" first then test_loadAfter.")
        //It is mandatory that loadInitial should invoked before load after , so executing test_loadInitial herer first then loadAfter.
        test_loadInitial()
        println("============================test_loadAfter==========================")
        addViewModelObserver()
        sourceFactory!!.getMutableLiveData().value!!.loadAfter(loadAfterParams, loadAfterCallback!!)
    }

    private fun addViewModelObserver() {
        println("Observer added into viewModel live data")
        if (!viewModel!!.getNetworkState()!!.hasActiveObservers()) {
            viewModel!!.getNetworkState()!!.observeForever(networkStateObserver)
        }
        if (!viewModel!!.getInitialLoading()!!.hasActiveObservers()) {
            viewModel!!.getInitialLoading()!!.observeForever(initialLoadObserver)
        }
        if (!viewModel!!.getPeopleLiveData()!!.hasActiveObservers()) {
            viewModel!!.getPeopleLiveData()!!.observeForever(pagedListObserver)
        }
    }

    private fun removeViewModelObserver() {
        println("ViewModel observer has been removed ")
        if (viewModel!!.getNetworkState()!!.hasActiveObservers()) {
            viewModel!!.getNetworkState()!!.removeObserver(networkStateObserver)
        }
        if (viewModel!!.getInitialLoading()!!.hasActiveObservers()) {
            viewModel!!.getInitialLoading()!!.removeObserver(initialLoadObserver)
        }

        if (viewModel!!.getPeopleLiveData()!!.hasActiveObservers()) {
            viewModel!!.getPeopleLiveData()!!.removeObserver(pagedListObserver)
        }
    }

    @Test
    fun test_retryPagination() {
        println("===========test_retryPagination=============")
        viewModel!!.retryPagination()
    }

}