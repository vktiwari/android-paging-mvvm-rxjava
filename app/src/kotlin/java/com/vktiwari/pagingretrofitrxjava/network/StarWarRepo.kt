package com.vktiwari.pagingretrofitrxjava.network

import com.vktiwari.pagingretrofitrxjava.model.People
import com.vktiwari.pagingretrofitrxjava.model.StarWarResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface StarWarRepo {
    @GET("people/")
    abstract fun getPeopleList(@Query("page") page: Int): Single<StarWarResponse<People>>
}