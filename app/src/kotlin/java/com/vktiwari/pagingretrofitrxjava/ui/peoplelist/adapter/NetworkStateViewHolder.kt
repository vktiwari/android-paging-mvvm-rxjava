package com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.vktiwari.pagingretrofitrxjava.R
import com.vktiwari.pagingretrofitrxjava.network.NetworkState

class NetworkStateViewHolder(itemView: View, retryCallback: RetryCallback) : RecyclerView.ViewHolder(itemView) {

    internal var textErrorMsg: TextView = itemView.findViewById(R.id.textErrorMsg)
    internal var btnRetry: Button = itemView.findViewById(R.id.buttonRetry)
    internal var loadingProgress: ProgressBar = itemView.findViewById(R.id.loadingProgress)

    init {
        btnRetry.setOnClickListener { v -> retryCallback.retry() }
    }

    fun bindTo(networkState: NetworkState) {
        //error message
        textErrorMsg.visibility = if (networkState.getMessage() != null) View.VISIBLE else View.GONE
        if (networkState.getMessage() != null) {
            textErrorMsg.setText(networkState.getMessage())
        }

        //loading and retry
        btnRetry.visibility = if (networkState.getStatus() === NetworkState.Status.FAILED) View.VISIBLE else View.GONE
        loadingProgress.visibility = if (networkState.getStatus() === NetworkState.Status.RUNNING) View.VISIBLE else View.GONE
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: RetryCallback): NetworkStateViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_network_state, parent, false)
            return NetworkStateViewHolder(view, retryCallback)
        }
    }

}