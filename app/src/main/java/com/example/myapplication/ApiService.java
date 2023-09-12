package com.example.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/search/{query}")
    Call<List<Book>> searchBooks(@Path("query") String query);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("books")
    Call<List<Book>> getAllBooks();

}



