package com.vktiwari.pagingretrofitrxjava.network;

import com.vktiwari.pagingretrofitrxjava.model.People;
import com.vktiwari.pagingretrofitrxjava.model.StarWarResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface StarWarAPI {
    @GET("people/")
    Single<StarWarResponse<People>> getPeopleList(@Query("page") int page);
}
