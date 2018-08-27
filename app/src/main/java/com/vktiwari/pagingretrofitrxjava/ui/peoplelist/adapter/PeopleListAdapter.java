package com.vktiwari.pagingretrofitrxjava.ui.peoplelist.adapter;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.vktiwari.pagingretrofitrxjava.R;
import com.vktiwari.pagingretrofitrxjava.model.People;
import com.vktiwari.pagingretrofitrxjava.network.NetworkState;

public class PeopleListAdapter extends PagedListAdapter<People, RecyclerView.ViewHolder> {
    private OnItemClickListener itemClickListener;
    private NetworkState networkState;
    private RetryCallback retryCallback;

    public PeopleListAdapter(OnItemClickListener itemClickListener, RetryCallback retryCallback) {
        super(DIFF_CALLBACK);
        this.retryCallback = retryCallback;
        this.itemClickListener = itemClickListener;

    }

    public static final DiffUtil.ItemCallback<People> DIFF_CALLBACK = new DiffUtil.ItemCallback<People>() {

        @Override
        public boolean areItemsTheSame(People oldItem, People newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(People oldItem, People newItem) {
            return true;
        }
    };


    private boolean hasExtraRow() {
        return networkState != null && networkState != NetworkState.LOADED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.item_people:
                return PeopleViewHolder.create(parent, itemClickListener);
            case R.layout.item_network_state:
                return NetworkStateViewHolder.create(parent, retryCallback);
                default:
                    throw new RuntimeException("Invalid view type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.item_network_state;
        } else {
            return R.layout.item_people;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.item_people:
                ((PeopleViewHolder) holder).bind(getItem(position));
                break;
            case R.layout.item_network_state:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasExtraRow() ? 1 : 0);
    }

    public void setNetworkState(NetworkState newNetworkState) {
        if (getCurrentList() != null) {
            if (getCurrentList().size() != 0) {
                NetworkState previousState = this.networkState;
                boolean hadExtraRow = hasExtraRow();
                this.networkState = newNetworkState;
                boolean hasExtraRow = hasExtraRow();
                if (hadExtraRow != hasExtraRow) {
                    if (hadExtraRow) {
                        notifyItemRemoved(super.getItemCount());
                    } else {
                        notifyItemInserted(super.getItemCount());
                    }
                } else if (hasExtraRow && previousState != newNetworkState) {
                    notifyItemChanged(getItemCount() - 1);
                }
            }
        }
    }
}

