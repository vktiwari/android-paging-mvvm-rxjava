package com.vktiwari.pagingretrofitrxjava.ui.peoplelist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.vktiwari.pagingretrofitrxjava.R
import com.vktiwari.pagingretrofitrxjava.datasource.PeopleDataSourceFactory
import com.vktiwari.pagingretrofitrxjava.model.People
import com.vktiwari.pagingretrofitrxjava.network.NetworkState
import com.vktiwari.pagingretrofitrxjava.ui.peopledetail.DetailActivity
import com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter.OnItemClickListener
import com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter.PeopleListAdapter
import com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter.RetryCallback
import kotlinx.android.synthetic.main.item_network_state.*
import kotlinx.android.synthetic.main.list_activity.*

class PeopleListActivity : AppCompatActivity(), RetryCallback, OnItemClickListener {

    private lateinit var peopleViewModel: PeopleListViewModel
    private lateinit var peopleListAdapter: PeopleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_activity)
        init()
    }

    fun init() {
        peopleViewModel = ViewModelProviders.of(this, MyViewModelFactory(PeopleDataSourceFactory())).get(PeopleListViewModel::class.java)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        peopleListAdapter = PeopleListAdapter(this, this)
        recyclerView.adapter = peopleListAdapter
        peopleViewModel.getPeopleLiveData()!!.observe(this, Observer { pageList -> peopleListAdapter!!.submitList(pageList) })
        peopleViewModel.getNetworkState()!!.observe(this, Observer { networkState -> peopleListAdapter!!.setNetworkState(networkState) })
        peopleViewModel.getInitialLoading()!!.observe(this, Observer { networkState ->
            if (networkState != null) {
                if (peopleListAdapter.currentList != null) {
                    if (peopleListAdapter.currentList!!.size > 0) {
                        swipeToRefresh!!.isRefreshing = networkState.getStatus() === NetworkState.LOADING.getStatus()
                    } else {
                        setInitialLoadingState(networkState)
                    }
                } else {
                    setInitialLoadingState(networkState)
                }
            }
        })
        buttonRetry.setOnClickListener { peopleViewModel.retryPagination() }
        swipeToRefresh!!.setOnRefreshListener { peopleViewModel.refresh() }
    }

    private fun setInitialLoadingState(networkState: NetworkState) {
        textErrorMsg.visibility = if (networkState.getMessage() != null) View.VISIBLE else View.GONE
        if (networkState.getMessage() != null) {
            textErrorMsg.text = networkState.getMessage()
        }
        buttonRetry.visibility = if (networkState.getStatus() === NetworkState.Status.FAILED) View.VISIBLE else View.GONE
        loadingProgress.visibility = if (networkState.getStatus() === NetworkState.Status.RUNNING) View.VISIBLE else View.GONE
        swipeToRefresh.isEnabled = networkState.getStatus() === NetworkState.Status.SUCCESS
    }

    override fun onClick(people: People) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("data", people)
        startActivity(intent)
    }

    override fun retry() {
        peopleViewModel.retryPagination()
    }
}
