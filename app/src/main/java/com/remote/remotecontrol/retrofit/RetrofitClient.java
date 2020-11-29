package com.remote.remotecontrol.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static RetrofitInterface mRetrofit;

    public static RetrofitInterface getClient(String baseUrl){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create()).
                    build();
            mRetrofit = retrofit.create(RetrofitInterface.class);
        }
        return mRetrofit;
    }
}
