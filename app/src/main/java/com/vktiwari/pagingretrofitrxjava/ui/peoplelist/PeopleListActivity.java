package com.vktiwari.pagingretrofitrxjava.ui.peoplelist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vktiwari.pagingretrofitrxjava.R;
import com.vktiwari.pagingretrofitrxjava.model.People;
import com.vktiwari.pagingretrofitrxjava.network.NetworkState;
import com.vktiwari.pagingretrofitrxjava.ui.peopledetail.DetailActivity;
import com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter.OnItemClickListener;
import com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter.PeopleListAdapter;
import com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter.RetryCallback;


public class PeopleListActivity extends AppCompatActivity implements RetryCallback, OnItemClickListener {

    private PeopleListViewModel peopleViewModel;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private TextView textErrorMsg;
    private Button btnRetry;
    private ProgressBar loadingProgress;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PeopleListAdapter peopleListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        init();
    }

    public void init() {
        peopleViewModel = ViewModelProviders.of(this).get(PeopleListViewModel.class);

        recyclerView = findViewById(R.id.my_recycler_view);
        loadingProgress = findViewById(R.id.loadingProgress);
        textErrorMsg = findViewById(R.id.textErrorMsg);
        btnRetry = findViewById(R.id.buttonRetry);
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        peopleListAdapter = new PeopleListAdapter(this, this);
        recyclerView.setAdapter(peopleListAdapter);
        peopleViewModel.getPeopleLiveData().observe(this, peopleListAdapter::submitList);
        peopleViewModel.getNetworkState().observe(this, peopleListAdapter::setNetworkState);
        peopleViewModel.getInitialLoading().observe(this, networkState -> {
            if (networkState != null) {
                if (peopleListAdapter.getCurrentList() != null) {
                    if (peopleListAdapter.getCurrentList().size() > 0) {
                        swipeRefreshLayout.setRefreshing(
                                networkState.getStatus() == NetworkState.LOADING.getStatus());
                    } else {
                        setInitialLoadingState(networkState);
                    }
                } else {
                    setInitialLoadingState(networkState);
                }
            }

        });
        btnRetry.setOnClickListener(v -> peopleViewModel.retryPagination());
        swipeRefreshLayout.setOnRefreshListener(() -> peopleViewModel.refresh());

    }


    private void setInitialLoadingState(NetworkState networkState) {
        textErrorMsg.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
        if (networkState.getMessage() != null) {
            textErrorMsg.setText(networkState.getMessage());
        }

        btnRetry.setVisibility(networkState.getStatus() == NetworkState.Status.FAILED ? View.VISIBLE : View.GONE);
        loadingProgress.setVisibility(networkState.getStatus() == NetworkState.Status.RUNNING ? View.VISIBLE : View.GONE);

        swipeRefreshLayout.setEnabled(networkState.getStatus() == NetworkState.Status.SUCCESS);

    }

    @Override
    public void onClick(People people) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("data", people);
        startActivity(intent);
    }

    @Override
    public void retry() {
        peopleViewModel.retryPagination();
    }
}
