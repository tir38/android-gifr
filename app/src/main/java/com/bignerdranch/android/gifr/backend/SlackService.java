package com.bignerdranch.android.gifr.backend;

import com.bignerdranch.android.gifr.backend.response.HistoryResponse;
import com.bignerdranch.android.gifr.backend.response.UserResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SlackService{

    @GET(API.HISTORY)
    void getHistory(@Query("token") String token, @Query("channel") String channel, Callback<HistoryResponse> responseCallback);

    @GET(API.USER)
    void getUser(@Query("token") String token, @Query("user") String channel, Callback<UserResponse> responseCallback);

    class API {
        public static final String HISTORY = "/channels.history";
        public static final String USER = "/users.info";
    }
}
