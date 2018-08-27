package com.vktiwari.pagingretrofitrxjava.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

public class StarWarResponse<T> implements Serializable {
    public int count;
    public String next;
    public String previous;
    public List<T> results;

    public boolean hasMore() {
        return !TextUtils.isEmpty(next);
    }
}
