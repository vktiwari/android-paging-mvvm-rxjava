package com.vktiwari.pagingretrofitrxjava.model;

import android.text.TextUtils
import java.io.Serializable

public class StarWarResponse<T> : Serializable {
    var count: Int = 0
    var next: String? = null
    var previous: String? = null
    var results: kotlin.collections.List<T>? = null

    fun hasMore(): Boolean {
        return !TextUtils.isEmpty(next)
    }
}
