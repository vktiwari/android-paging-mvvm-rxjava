package com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vktiwari.pagingretrofitrxjava.R;
import com.vktiwari.pagingretrofitrxjava.network.NetworkState;

public class NetworkStateViewHolder extends RecyclerView.ViewHolder {

    TextView textErrorMsg;
    Button btnRetry;
    ProgressBar loadingProgress;

    private NetworkStateViewHolder(View itemView, RetryCallback retryCallback) {
        super(itemView);
        textErrorMsg = itemView.findViewById(R.id.textErrorMsg);
        btnRetry = itemView.findViewById(R.id.buttonRetry);
        loadingProgress = itemView.findViewById(R.id.loadingProgress);

        btnRetry.setOnClickListener(v -> retryCallback.retry());
    }

    public void bindTo(NetworkState networkState) {
        //error message
        textErrorMsg.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
        if (networkState.getMessage() != null) {
            textErrorMsg.setText(networkState.getMessage());
        }

        //loading and retry
        btnRetry.setVisibility(networkState.getStatus() == NetworkState.Status.FAILED ? View.VISIBLE : View.GONE);
        loadingProgress.setVisibility(networkState.getStatus() == NetworkState.Status.RUNNING ? View.VISIBLE : View.GONE);
    }

    public static NetworkStateViewHolder create(ViewGroup parent, RetryCallback retryCallback) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_network_state, parent, false);
        return new NetworkStateViewHolder(view, retryCallback);
    }

}

